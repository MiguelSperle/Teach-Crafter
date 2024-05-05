package com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetToken;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordUserNotLoggedDTO(
        @NotBlank(message = "A new password is required to reset your current password")
        String newPassword,
        @NotBlank(message = "The token is required to reset your current password")
        String token
) {
}
