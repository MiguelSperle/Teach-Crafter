package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.services.PasswordResetTokensService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
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
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.passwordResetTokensService.resetPasswordUserNotLogged(resetPasswordUserNotLoggedDTO);

        return ResponseEntity.ok().body(new MessageResponseDTO("Password reset successfully", HttpStatus.OK.value()));
    }
}
