package com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePasswordResetTokenDTO (
        @NotBlank(message = "Your current email is required to reset your current password")
        @Email(message = "The field [currentEmail] must contain a valid email")
        String currentEmail
) {
}
