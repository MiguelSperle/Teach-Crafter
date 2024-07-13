package com.miguelsperle.teach_crafter.utils.integration;

import com.github.javafaker.Faker;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;

public class CoursesUtils {
    public static CoursesEntity createCourse(UsersEntity user, Faker faker) {
        return CoursesEntity.builder().usersEntity(user).maximumAttendees(10)
                .description(faker.lorem().paragraph()) // GENERATE A RANDOM DESCRIPTION FOR THE COURSE
                .name(faker.educator().course()) // GENERATE A RANDOM NAME FOR THE COURSE
                .build();
    }
}
