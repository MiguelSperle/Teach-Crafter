package com.miguelsperle.teach_crafter.modules.users.dtos.authorization;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorizationResponseDTO(
        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ")
        String token,
        @Schema(example = "200")
        int status
) {
}
