package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import java.time.LocalDateTime;

public record CourseResponseDTO(String id, String name, String description, Integer maximumAttendees, LocalDateTime createdAt, String creator) {
}
