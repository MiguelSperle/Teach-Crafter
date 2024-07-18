package com.miguelsperle.teach_crafter.dtos.general;

import io.swagger.v3.oas.annotations.media.Schema;

public record CustomAuthenticationEntryResponseDTO(
        @Schema(example = "Authorization token missing in request header", description = "The message is dynamic and can vary based on the request")
        String message,
        @Schema(example = "403")
        int status
) {
}