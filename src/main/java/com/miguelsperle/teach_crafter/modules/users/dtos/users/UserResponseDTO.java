package com.miguelsperle.teach_crafter.modules.users.dtos.users;

import java.time.LocalDateTime;

public record UserResponseDTO(
        String username,

        String role,
        String name,
        String email,
        String avatar,
        LocalDateTime createdAt
) {
}
