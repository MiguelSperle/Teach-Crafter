package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailUserDTO(
        @NotBlank(message = "A new email is required to update your current email")
        @Email(message = "The field [newEmail] must contain a valid email")
        String newEmail,

        @NotBlank(message = "Your current password is required to update your current email")
        String currentPassword
) {
}
