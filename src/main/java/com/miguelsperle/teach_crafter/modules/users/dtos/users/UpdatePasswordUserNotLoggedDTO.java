package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordUserNotLoggedDTO(
        @NotBlank(message = "A new password is required to update your current password")
        String newPassword,
        @NotBlank(message = "The token is required to update your current password")
        String token
) {
}
