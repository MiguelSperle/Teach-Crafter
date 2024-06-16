package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.AuthorizationResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.authorization.UsersAuthorizationDTO;
import com.miguelsperle.teach_crafter.modules.users.services.UsersAuthorizationService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UsersAuthorizationController {
    private final UsersAuthorizationService usersAuthorizationService;
    private final RequestFieldValidationService requestFieldValidationService;

    public UsersAuthorizationController(final UsersAuthorizationService usersAuthorizationService, final RequestFieldValidationService requestFieldValidationService) {
        this.usersAuthorizationService = usersAuthorizationService;
        this.requestFieldValidationService = requestFieldValidationService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login, token generation, and access control", description = "This route is responsible for handling user login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorizationResponseDTO.class))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Email", description = "Error returned because the email is missing in the request",  value = "{\"message\": \"Email is required to login\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Password", description = "Error returned because password is missing in the request", value = "{\"message\": \"Password is required to login\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid Email", description = "Error returned because the email does not contain a valid value", value = "{\"message\": \"The field [email] must contain a valid email\", \"status\": 400}"),
            })),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Incorrect Credentials", description = "Error returned because user tried to login but they put incorrect credentials",value = "{\"message\": \"Email and/or password incorrect\", \"status\": 401}")
                    })),
    })
    public ResponseEntity<Object> usersAuthorization(@RequestBody @Valid UsersAuthorizationDTO usersAuthorizationDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        String token = this.usersAuthorizationService.usersAuthorization(usersAuthorizationDTO);

        return ResponseEntity.ok().body(new AuthorizationResponseDTO(token, HttpStatus.OK.value()));
    }
}
