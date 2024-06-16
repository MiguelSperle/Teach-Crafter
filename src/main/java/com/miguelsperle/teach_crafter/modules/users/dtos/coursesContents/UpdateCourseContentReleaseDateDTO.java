package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateCourseContentReleaseDateDTO(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A new content release date is required to update the current content release date")
        LocalDate newContentReleaseDate
) {
}
