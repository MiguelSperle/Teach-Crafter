package com.miguelsperle.teach_crafter.dtos.general;

import io.swagger.v3.oas.annotations.media.Schema;

public record CustomAuthenticationEntryResponseDTO(
        @Schema(example = "Authorization token missing in request header")
        String message,
        @Schema(example = "403")
        int status
) {
}