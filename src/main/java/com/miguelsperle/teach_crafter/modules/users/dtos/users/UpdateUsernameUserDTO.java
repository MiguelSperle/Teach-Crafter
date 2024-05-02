package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameUserDTO(
        @NotBlank(message = "A new username is required to update your current username")
        String newUsername
) {
}
