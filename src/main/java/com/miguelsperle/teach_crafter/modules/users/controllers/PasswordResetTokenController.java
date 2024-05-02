package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken.CreatePasswordResetTokenDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.users.UpdatePasswordUserNotLoggedDTO;
import com.miguelsperle.teach_crafter.modules.users.services.PasswordResetTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reset-password")
@RequiredArgsConstructor
public class PasswordResetTokenController {
    private final PasswordResetTokenService passwordResetTokensService;

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendResetPasswordEmail(@RequestBody @Valid CreatePasswordResetTokenDTO createPasswordResetTokenDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.passwordResetTokensService.createPasswordResetToken(createPasswordResetTokenDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("You must check your e-mail", HttpStatus.CREATED.value()));
    }

    @PutMapping
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid UpdatePasswordUserNotLoggedDTO updatePasswordUserNotLoggedDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(String.valueOf(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .findFirst().get()), HttpStatus.BAD_REQUEST.value()));
        }

        this.passwordResetTokensService.updatePasswordUserNotLogged(updatePasswordUserNotLoggedDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Password updated successfully", HttpStatus.OK.value()));
    }
}
