package com.miguelsperle.teach_crafter.dtos.general;

import io.swagger.v3.oas.annotations.media.Schema;

public record CustomAccessDeniedHandlerResponseDTO(
        @Schema(example = "Access to this resource is restricted")
        String message,
        @Schema(example = "403")
        int status
) {
}
