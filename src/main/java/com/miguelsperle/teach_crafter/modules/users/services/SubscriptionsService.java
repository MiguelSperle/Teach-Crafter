package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions.SubscriptionAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.NoAvailableSpotsException;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions.SubscriptionNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.SubscriptionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SubscriptionsService {
    private final SubscriptionsRepository subscriptionsRepository;
    private final UsersService usersService;
    private final SubscriptionsCoursesManager subscriptionsCoursesManager;

    public SubscriptionsService(
            final SubscriptionsRepository subscriptionsRepository,
            final UsersService usersService,
            final SubscriptionsCoursesManager subscriptionsCoursesManager
    ) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.usersService = usersService;
        this.subscriptionsCoursesManager = subscriptionsCoursesManager;
    }

    private List<SubscriptionsEntity> getAllSubscriptionsByCourseId(String courseId) {
        return this.subscriptionsRepository.findAllByCoursesEntityId(courseId);
    }

    public SubscriptionsEntity createCourseSubscription(String courseId) {
        SubscriptionsEntity newSubscription = new SubscriptionsEntity();

        this.verifyUserIsNotCourseOwner(courseId);

        this.verifyAvailableSpots(courseId);

        this.verifySubscriptionExistsForTheCourse(courseId);

        newSubscription.setUsersEntity(this.usersService.getUserAuthenticated());
        newSubscription.setCoursesEntity(this.subscriptionsCoursesManager.getCourseById(courseId));

        return this.subscriptionsRepository.save(newSubscription);
    }

    private void verifyUserIsNotCourseOwner(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        CoursesEntity course = this.subscriptionsCoursesManager.getCourseById(courseId);

        if (Objects.equals(user.getId(), course.getUsersEntity().getId())) {
            throw new TaskDeniedException("User is the owner of the course and cannot subscribe to it");
        }
    }

    private void verifyAvailableSpots(String courseId) {
        List<SubscriptionsEntity> subscriptions = this.getAllSubscriptionsByCourseId(courseId);

        CoursesEntity course = this.subscriptionsCoursesManager.getCourseById(courseId);

        if (Objects.equals(course.getMaximumAttendees() - subscriptions.size(), 0)) {
            throw new NoAvailableSpotsException("No available spots");
        }
    }

    private void verifySubscriptionExistsForTheCourse(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        Optional<SubscriptionsEntity> subscription = this.getSubscriptionByUserIdAndCourseId(user.getId(), courseId);

        if (subscription.isPresent())
            throw new SubscriptionAlreadyExistsException("You have already subscribed in this course");
    }

    private Optional<SubscriptionsEntity> getSubscriptionByUserIdAndCourseId(String userId, String courseId) {
        return this.subscriptionsRepository.findByUsersEntityIdAndCoursesEntityId(userId, courseId);
    }

    @Transactional
    public void deleteCourseSubscription(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        this.ensureUserIsNotSubscribed(user.getId(), courseId);

        this.subscriptionsRepository.deleteByUsersEntityIdAndCoursesEntityId(user.getId(), courseId);
    }

    public void ensureUserIsNotSubscribed(String userId, String courseId) {
        this.getSubscriptionByUserIdAndCourseId(userId, courseId).orElseThrow(() -> new SubscriptionNotFoundException("Subscription does not exist"));
    }
}
