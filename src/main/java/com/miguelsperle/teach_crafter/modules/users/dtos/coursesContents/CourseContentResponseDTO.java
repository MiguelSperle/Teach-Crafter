package com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CourseContentResponseDTO(String id, String description, String videoUrl, String status, LocalDate releaseDate, String courseModule, LocalDateTime createdAt) {
}
