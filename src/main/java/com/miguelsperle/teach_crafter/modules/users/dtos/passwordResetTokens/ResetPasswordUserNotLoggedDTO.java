package com.miguelsperle.teach_crafter.modules.users.dtos.passwordResetTokens;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordUserNotLoggedDTO(
        @Schema(example = "1234567", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A new password is required to reset your current password")
        String newPassword,
        @Schema(example = "IPWiVMOlkUO_RGsUaKHyT7n_c29_JDCz", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "The token is required to reset your current password")
        String token
) {
}
