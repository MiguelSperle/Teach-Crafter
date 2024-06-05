package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.CourseNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.SubscriptionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionsCoursesManager {
    private final CoursesRepository coursesRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    public SubscriptionsCoursesManager(
            final CoursesRepository coursesRepository,
            final SubscriptionsRepository subscriptionsRepository
    ) {
        this.coursesRepository = coursesRepository;
        this.subscriptionsRepository = subscriptionsRepository;
    }

    public CoursesEntity getCourseById(String courseId) {
        return this.coursesRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course not found"));
    }

    public List<SubscriptionsEntity> getAllSubscriptionsByCourseId(String courseId) {
        return this.subscriptionsRepository.findAllByCoursesEntityId(courseId);
    }

    public List<SubscriptionsEntity> getAllSubscriptionsByUserId(String userId) {
        return this.subscriptionsRepository.findAllByUsersEntityId(userId);
    }

}
