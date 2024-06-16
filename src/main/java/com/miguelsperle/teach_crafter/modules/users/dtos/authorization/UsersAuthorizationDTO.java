package com.miguelsperle.teach_crafter.modules.users.dtos.authorization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsersAuthorizationDTO (
        @Schema(example = "example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required to login")
        @Email(message = "The field [email] must contain a valid email")
        String email,

        @Schema(example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required to login")
        String password
) {
}
