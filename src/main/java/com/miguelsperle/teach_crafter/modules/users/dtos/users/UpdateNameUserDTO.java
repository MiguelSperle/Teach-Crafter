package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameUserDTO(
        @NotBlank(message = "A new name is required to update your current name")
        String newName
) {
}
