package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CreateUserDTO(
        @Schema(example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Username is required to create an account")
        @Pattern(regexp = "^\\S+$", message = "The field [username] must not contain space")
        String username,

        @Schema(example = "ROLE_USER OR ROLE_CREATOR",  requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Role is required to create an account")
        String role,

        @Schema(example = "Example", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Name is required to create an account")
        String name,

        @Schema(example = "example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required to create an account")
        @Email(message = "The field [email] must contain a valid email")
        String email,

        @Schema(example = "12345", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5, maxLength = 100)
        @NotBlank(message = "Password is required to create an account")
        @Length(min = 5, max = 100, message = "Password must has between 5 and 100 character")
        String password
) {
}
