package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import java.time.LocalDateTime;

public record CoursesSubscribedResponseDTO(
        String id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime subscriptionCreatedAt,
        String createdBy
){}
