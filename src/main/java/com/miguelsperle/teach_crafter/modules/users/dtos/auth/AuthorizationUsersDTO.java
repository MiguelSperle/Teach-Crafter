package com.miguelsperle.teach_crafter.modules.users.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthorizationUsersDTO(
        @NotBlank(message = "Email is required to login")
        String email,
        @NotBlank(message = "Password is required to login")
        String password
) {
}
