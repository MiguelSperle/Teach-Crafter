package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CreateUserDTO(
        @NotBlank(message = "Username is required")
        @Pattern(regexp = "^\\S+$", message = "The field [username] must not contain space")
        String username,

        @NotBlank(message = "Role is required")
        String role,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "The field [email] must contain a valid email")
        String email,

        @NotBlank(message = "Password is required")
        @Length(min = 5, max = 100, message = "Password must has between 5 and 100 character")
        String password
) {
}
