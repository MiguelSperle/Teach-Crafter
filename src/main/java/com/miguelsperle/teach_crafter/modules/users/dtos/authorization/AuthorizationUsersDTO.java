package com.miguelsperle.teach_crafter.modules.users.dtos.authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthorizationUsersDTO(
        @NotBlank(message = "Email is required to login")
        @Email(message = "The field [email] must contain a valid email")
        String email,
        @NotBlank(message = "Password is required to login")
        String password
) {
}
