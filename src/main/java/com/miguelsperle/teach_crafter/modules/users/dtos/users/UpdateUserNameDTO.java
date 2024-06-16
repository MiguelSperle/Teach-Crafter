package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserNameDTO(
        @Schema(example = "Cheese Computer", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new name is required to update your current name")
        String newName
) {
}