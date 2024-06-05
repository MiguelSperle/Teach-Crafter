package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.NoAvailableSpotsException;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.SubscriptionsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions.CourseSubscriptionAlreadyExistsException;
import com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions.SubscriptionNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.repositories.SubscriptionsRepository;
import com.miguelsperle.teach_crafter.utils.mocks.CoursesEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.SubscriptionsEntityCreator;
import com.miguelsperle.teach_crafter.utils.mocks.UsersEntityCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionsServiceTest {
    @InjectMocks
    private SubscriptionsService subscriptionsService;

    @Mock
    private SubscriptionsRepository subscriptionsRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private SubscriptionsCoursesManager subscriptionsCoursesManager;

    @BeforeEach
    public void setUp() {
        when(this.usersService.getUserAuthenticated()).thenReturn(UsersEntityCreator.createValidAuthenticatedUsersEntity());
    }

    @Test
    @DisplayName("User should be able to create a subscription in some course")
    public void user_should_be_able_to_create_a_subscription() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.subscriptionsCoursesManager.getCourseById(any())).thenReturn(course);

        when(this.subscriptionsRepository.findAllByCoursesEntityId(any())).thenReturn(Collections.emptyList());

        when(this.subscriptionsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.empty());

        when(this.subscriptionsRepository.save(any(SubscriptionsEntity.class))).thenReturn(SubscriptionsEntityCreator.createSubscriptionsEntityToBeSaved());

        SubscriptionsEntity newSubscription = this.subscriptionsService.createCourseSubscription(course.getId());

        assertNotNull(newSubscription.getId());
        assertThat(newSubscription).hasFieldOrProperty("id");
    }

    @Test
    @DisplayName("User should not be able to create a subscription if the same individual is the course owner")
    public void user_should_not_be_able_to_create_a_subscription_if_the_same_individual_is_the_course_owner() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());

        when(this.subscriptionsCoursesManager.getCourseById(any())).thenReturn(course);

        TaskDeniedException exception = assertThrows(TaskDeniedException.class, () -> {
            this.subscriptionsService.createCourseSubscription(course.getId());
        });

        String expectedErrorMessage = "User is the owner of the course and cannot subscribe to it";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("User should not be able to create a subscription if the course does not have available spots")
    public void user_should_not_be_able_to_create_a_subscription_if_the_course_does_not_have_available_spots() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        SubscriptionsEntity subscription = SubscriptionsEntityCreator.createValidSubscriptionsEntity();
        subscription.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        subscription.setCoursesEntity(course);

        List<SubscriptionsEntity> existingSubscription = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            existingSubscription.add(subscription);
        }

        when(this.subscriptionsRepository.findAllByCoursesEntityId(any())).thenReturn(existingSubscription);

        when(this.subscriptionsCoursesManager.getCourseById(any())).thenReturn(course);

        NoAvailableSpotsException exception = assertThrows(NoAvailableSpotsException.class, () -> {
            this.subscriptionsService.createCourseSubscription(course.getId());
        });

        String expectedErrorMessage = "No available spots";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("User should not be able to create a subscription if the same individual is already subscribed in the course")
    public void user_should_not_be_able_to_create_a_subscription_if_the_same_individual_is_already_subscribed_in_the_course() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.subscriptionsCoursesManager.getCourseById(any())).thenReturn(course);

        SubscriptionsEntity subscription = SubscriptionsEntityCreator.createValidSubscriptionsEntity();
        subscription.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        subscription.setCoursesEntity(course);

        when(this.subscriptionsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.of(subscription));

        CourseSubscriptionAlreadyExistsException exception = assertThrows(CourseSubscriptionAlreadyExistsException.class, () -> {
            this.subscriptionsService.createCourseSubscription(course.getId());
        });

        String expectedErrorMessage = "You have already subscribed in this course";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("User should be able to delete a subscription of a course")
    public void user_should_be_able_to_delete_a_subscription_of_a_course() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        SubscriptionsEntity subscription = SubscriptionsEntityCreator.createValidSubscriptionsEntity();
        subscription.setUsersEntity(UsersEntityCreator.createValidAuthenticatedUsersEntity());
        subscription.setCoursesEntity(course);

        when(this.subscriptionsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.of(subscription));

        this.subscriptionsService.deleteCourseSubscription(course.getId());

        // Verify if the method save was called with a specific argument
        verify(this.subscriptionsRepository).deleteByUsersEntityIdAndCoursesEntityId(UsersEntityCreator.createValidAuthenticatedUsersEntity().getId(), course.getId());
    }

    @Test
    @DisplayName("User should not be able to delete a subscription of a course if the same individual is not subscribed in the course")
    public void user_should_not_be_able_to_delete_a_subscription_of_a_course_if_the_same_individual_is_not_subscribed_in_the_course() {
        CoursesEntity course = CoursesEntityCreator.createValidCoursesEntity();
        course.setUsersEntity(UsersEntityCreator.createSecondValidUsersEntity());

        when(this.subscriptionsRepository.findByUsersEntityIdAndCoursesEntityId(any(), any())).thenReturn(Optional.empty());

        SubscriptionNotFoundException exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            this.subscriptionsService.deleteCourseSubscription(course.getId());
        });

        String expectedErrorMessage = "Subscription does not exist";

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
