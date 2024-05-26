package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions.CourseSubscriptionAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.NoAvailableSpotsException;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions.SubscriptionNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.SubscriptionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SubscriptionsService {
    @Autowired
    private SubscriptionsRepository subscriptionRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    @Lazy
    private CoursesService coursesService;

    public List<SubscriptionsEntity> getAllSubscriptionsByCourseId(String courseId) {
        return this.subscriptionRepository.findAllByCoursesEntityId(courseId);
    }

    public List<SubscriptionsEntity> getAllSubscriptionsByUserId(String userId) {
        return this.subscriptionRepository.findAllByUsersEntityId(userId);
    }

    public void createCourseSubscription(String courseId) {
        SubscriptionsEntity newSubscription = new SubscriptionsEntity();

        this.verifyUserIsNotCourseOwner(courseId);

        this.verifyAvailableSpots(courseId);

        this.verifySubscriptionExistsForTheCourse(courseId);

        newSubscription.setUsersEntity(this.usersService.getUserAuthenticated());
        newSubscription.setCoursesEntity(this.coursesService.getCourseById(courseId));

        this.subscriptionRepository.save(newSubscription);
    }

    private void verifyUserIsNotCourseOwner(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        CoursesEntity course = this.coursesService.getCourseById(courseId);

        if (Objects.equals(user.getId(), course.getUsersEntity().getId())) {
            throw new TaskDeniedException("User is the owner of the course and cannot subscribe to it");
        }
    }

    private void verifyAvailableSpots(String courseId) {
        List<SubscriptionsEntity> subscriptions = this.getAllSubscriptionsByCourseId(courseId);

        CoursesEntity course = this.coursesService.getCourseById(courseId);

        if (Objects.equals(course.getMaximumAttendees() - subscriptions.size(), 0)) {
            throw new NoAvailableSpotsException("No available spots");
        }
    }

    private void verifySubscriptionExistsForTheCourse(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        Optional<SubscriptionsEntity> subscription = this.getSubscriptionByUserIdAndCourseId(user.getId(), courseId);

        if (subscription.isPresent())
            throw new CourseSubscriptionAlreadyExistsException("You have already subscribed in this course");
    }

    private Optional<SubscriptionsEntity> getSubscriptionByUserIdAndCourseId(String userId, String courseId) {
        return this.subscriptionRepository.findByUsersEntityIdAndCoursesEntityId(userId, courseId);
    }

    @Transactional
    public void deleteCourseSubscription(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        this.verifyUserIsNotSubscribed(user.getId(), courseId);

        this.subscriptionRepository.deleteByUsersEntityIdAndCoursesEntityId(user.getId(), courseId);
    }

    private void verifyUserIsNotSubscribed(String userId, String courseId) {
        Optional<SubscriptionsEntity> subscription = this.getSubscriptionByUserIdAndCourseId(userId, courseId);

        if (subscription.isEmpty()) throw new SubscriptionNotFoundException("Subscription does not exist");
    }
}
