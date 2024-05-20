package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CourseResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseNameDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.CourseNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.subscription.SubscriptionEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CoursesService {
    @Autowired
    private CoursesRepository coursesRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    @Lazy
    private SubscriptionService subscriptionService;

    public void createCourse(CreateCourseDTO createCourseDTO) {
        CoursesEntity newCourse = new CoursesEntity();

        this.verifyCreatorUserReachedCourseCreationLimit();

        newCourse.setName(createCourseDTO.name());
        newCourse.setDescription(createCourseDTO.description());
        newCourse.setMaximumAttendees(createCourseDTO.maximumAttendees());
        newCourse.setUsersEntity(this.usersService.getUserAuthenticated());

        this.coursesRepository.save(newCourse);
    }

    private void verifyCreatorUserReachedCourseCreationLimit() {
        UsersEntity user = this.usersService.getUserAuthenticated();

        List<CoursesEntity> courses = this.getAllCoursesByCreatorUserId(user.getId());

        final int maxCourseCreationLimit = 5;

        if (Objects.equals(courses.size(), maxCourseCreationLimit)) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    private List<CoursesEntity> getAllCoursesByCreatorUserId(String userId) {
        return this.coursesRepository.findAllByUsersEntityId(userId);
    }

    public CoursesEntity getCourseById(String courseId) {
        return this.coursesRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException("Course not found"));
    }

    public void updateCourseName(UpdateCourseNameDTO updateCourseNameDTO, String courseId) {
        CoursesEntity course = this.getCourseById(courseId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        course.setName(updateCourseNameDTO.newName());

        this.coursesRepository.save(course);
    }

    public void updateCourseDescription(UpdateCourseDescriptionDTO updateCourseDescriptionDTO, String courseId) {
        CoursesEntity course = this.getCourseById(courseId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        course.setDescription(updateCourseDescriptionDTO.newDescription());

        this.coursesRepository.save(course);
    }

    private void verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(String courseId) {
        CoursesEntity course = this.getCourseById(courseId);

        UsersEntity user = this.usersService.getUserAuthenticated();

        if (!Objects.equals(course.getUsersEntity().getId(), user.getId())) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    public List<CourseResponseDTO> getAllCoursesCreatedByCreatorUser() {
        UsersEntity user = this.usersService.getUserAuthenticated();

        return this.getAllCoursesByCreatorUserId(user.getId()).stream().map(coursesEntity -> {
            List<SubscriptionEntity> subscriptions = this.subscriptionService.getAllSubscriptionsByCourseId(coursesEntity.getId());

            int numberAvailableSpots = Math.max(0, coursesEntity.getMaximumAttendees() - subscriptions.size());

            int amountSubscription = subscriptions.size();

            return new CourseResponseDTO(
                    coursesEntity.getId(),
                    coursesEntity.getName(),
                    coursesEntity.getDescription(),
                    coursesEntity.getMaximumAttendees(),
                    numberAvailableSpots,
                    amountSubscription,
                    coursesEntity.getCreatedAt(),
                    coursesEntity.getUsersEntity().getName()
            );
        }).toList();
    }

    public void deactivateCourse(String courseId) {
        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        this.coursesRepository.deleteById(courseId);

        this.subscriptionService.deleteAllSubscriptionsForTheCourseIfExist(courseId);
    }

    public List<CourseResponseDTO> getCoursesByDescriptionKeyword(String descriptionKeyword) {
        return this.coursesRepository.findByDescriptionContainingIgnoreCase(descriptionKeyword).stream().map(coursesEntity -> {
            List<SubscriptionEntity> subscriptions = this.subscriptionService.getAllSubscriptionsByCourseId(coursesEntity.getId());

            int numberAvailableSpots = Math.max(0, coursesEntity.getMaximumAttendees() - subscriptions.size());

            int amountSubscription = subscriptions.size();

            return new CourseResponseDTO(
                    coursesEntity.getId(),
                    coursesEntity.getName(),
                    coursesEntity.getDescription(),
                    coursesEntity.getMaximumAttendees(),
                    numberAvailableSpots,
                    amountSubscription,
                    coursesEntity.getCreatedAt(),
                    coursesEntity.getUsersEntity().getName()
            );
        }).toList();
    }
}
