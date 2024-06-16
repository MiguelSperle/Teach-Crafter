package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateLoggedUserPasswordDTO(
        @Schema(example = "1234567", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new password is required to update your current password")
        @Length(min = 5, max = 100, message = "Password must has between 5 and 100 character")
        String newPassword,

        @Schema(example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Your current password is required to update your password")
        String currentPassword
) {
}
