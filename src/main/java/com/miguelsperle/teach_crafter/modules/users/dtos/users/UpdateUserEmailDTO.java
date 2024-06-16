package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserEmailDTO(
        @Schema(example = "alien_example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new email is required to update your current email")
        @Email(message = "The field [newEmail] must contain a valid email")
        String newEmail,

        @Schema(example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Your current password is required to update your email")
        String currentPassword
) {
}
