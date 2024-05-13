package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.ResetPasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions.ActivePasswordResetTokenException;
import com.miguelsperle.teach_crafter.modules.users.services.PasswordResetTokenService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reset-password")
@RequiredArgsConstructor
public class PasswordResetTokenController {
    private final PasswordResetTokenService passwordResetTokensService;
    private final RequestFieldValidationService requestFieldValidationService;

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendResetPasswordEmail(@RequestBody @Valid CreatePasswordResetTokenDTO createPasswordResetTokenDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        try {
            this.passwordResetTokensService.createPasswordResetToken(createPasswordResetTokenDTO);
        } catch (ActivePasswordResetTokenException exception) {
            return ResponseEntity.ok().body(new MessageResponseDTO(exception.getMessage(), HttpStatus.OK.value()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDTO("Please check your email", HttpStatus.OK.value()));
    }

    @PutMapping
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid ResetPasswordUserNotLoggedDTO resetPasswordUserNotLoggedDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.passwordResetTokensService.resetPasswordUserNotLogged(resetPasswordUserNotLoggedDTO);

        return ResponseEntity.ok().body(new MessageResponseDTO("Password reset successfully", HttpStatus.OK.value()));
    }
}
