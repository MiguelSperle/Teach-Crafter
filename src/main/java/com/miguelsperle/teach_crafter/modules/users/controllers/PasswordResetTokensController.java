package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.services.PasswordResetTokensService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reset-password")
public class PasswordResetTokensController {
    private final PasswordResetTokensService passwordResetTokensService;
    private final RequestFieldValidationService requestFieldValidationService;

    public PasswordResetTokensController(final PasswordResetTokensService passwordResetTokensService, final RequestFieldValidationService requestFieldValidationService) {
        this.passwordResetTokensService = passwordResetTokensService;
        this.requestFieldValidationService = requestFieldValidationService;
    }

    @PostMapping("/send-email")
    @Operation(summary = "Start password recovery", description = "This route is responsible for sending a reset password email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"You have an active password reset token. Please check your email to continue with password recovery\", \"status\": 200}"),
                    })),
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Please check your email\", \"status\": 201}"),
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Current Email", description = "Error returned because the current email is missing in the request", value = "{\"message\": \"Your current email is required to create a token to reset your current password\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid Current Email", description = "Error returned because the current email does not contain a valid value", value = "{\"message\": \"The field [currentEmail] must contain a valid email\", \"status\": 400}")
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No user found", description = "Error returned because no user was found with the specific email address", value = "{\"message\": \"User not found\", \"status\": 404}")
                    }))
    })
    public ResponseEntity<Object> sendResetPasswordEmail(@RequestBody @Valid CreatePasswordResetTokenDTO createPasswordResetTokenDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        try {
            this.passwordResetTokensService.createPasswordResetToken(createPasswordResetTokenDTO);
        } catch (ActivePasswordResetTokenException exception) {
            return ResponseEntity.ok().body(new MessageResponseDTO(exception.getMessage(), HttpStatus.OK.value()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("Please check your email", HttpStatus.CREATED.value()));
    }

    @PutMapping
    @Operation(summary = "Complete password recovery", description = "This route is responsible for allowing a user not logged to reset their password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Password reset successfully\", \"status\": 200}"),
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Password", description = "Error returned because the new password is missing in the request", value = "{\"message\": \"A new password is required to reset your current password\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Token", description = "Error returned because the token is missing in the request", value = "{\"message\": \"The token is required to reset your current password\", \"status\": 400}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Password reset token expired", description = "Error returned because user tried to reset their password, but the token was already expired", value = "{\"message\": \"The password reset token has already expired. Please make the process again\", \"status\": 403}"),
                    }))
    })
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.passwordResetTokensService.resetPasswordUserNotLogged(resetPasswordUserNotLoggedDTO);

        return ResponseEntity.ok().body(new MessageResponseDTO("Password reset successfully", HttpStatus.OK.value()));
    }
}
