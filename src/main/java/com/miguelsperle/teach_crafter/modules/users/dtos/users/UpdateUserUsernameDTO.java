package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserUsernameDTO(
        @Schema(example = "alien example", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new username is required to update your current username")
        @Pattern(regexp = "^\\S+$", message = "The field [newUsername] must not contain space")
        String newUsername,

        @Schema(example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Your current password is required to update your username")
        String currentPassword
) {
}
