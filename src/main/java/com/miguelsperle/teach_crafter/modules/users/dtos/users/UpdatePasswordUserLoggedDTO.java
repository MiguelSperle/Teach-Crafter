package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordUserLoggedDTO(
        @NotBlank(message = "A new password is required to update your current password")
        String newPassword,

        @NotBlank(message = "Your current password is required")
        String currentPassword
) {
}
