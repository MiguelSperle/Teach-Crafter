package com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePasswordResetTokenDTO (
        @Schema(example = "example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Your current email is required to create a token to reset your current password")
        @Email(message = "The field [currentEmail] must contain a valid email")
        String currentEmail
) {
}
