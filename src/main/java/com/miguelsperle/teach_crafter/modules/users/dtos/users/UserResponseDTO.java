package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponseDTO(
        @Schema(example = "example")
        String username,
        @Schema(example = "ROLE_CREATOR")
        String role,
        @Schema(example = "Example")
        String name,
        @Schema(example = "example@gmail.com")
        String email,
        @Schema(example = "USER_IMAGE_URL")
        String avatar,
        @Schema(example = "2024-05-15T17:23:10.904732")
        LocalDateTime createdAt
) {
}
