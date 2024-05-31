package com.miguelsperle.teach_crafter.utils.mocks;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

import java.time.LocalDateTime;

public class SubscriptionsEntityCreator {
    public static SubscriptionsEntity createValidSubscriptionsEntity() {
        return SubscriptionsEntity
                .builder()
                .id("1")
                .usersEntity(new UsersEntity())
                .coursesEntity(new CoursesEntity())
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

    }
}
