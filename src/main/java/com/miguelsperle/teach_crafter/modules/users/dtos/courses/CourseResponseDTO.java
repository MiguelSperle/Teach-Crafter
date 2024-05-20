package com.miguelsperle.teach_crafter.modules.users.dtos.courses;

import java.time.LocalDateTime;

public record CourseResponseDTO(
        String id,
        String name,
        String description,
        int  maximumAttendees,
        int numberAvailableSpots,
        int amountSubscription,
        LocalDateTime createdAt,
        String createdBy
){}
