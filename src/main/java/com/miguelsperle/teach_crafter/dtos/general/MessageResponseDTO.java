package com.miguelsperle.teach_crafter.dtos.general;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponseDTO(
        @Schema(description = "The message is dynamic and can vary based on the request")
        String message,
        @Schema(description = "The status is dynamic and can vary based on the request")
        int status
) {
}