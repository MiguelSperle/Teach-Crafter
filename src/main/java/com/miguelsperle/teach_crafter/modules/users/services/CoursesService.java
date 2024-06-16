package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.*;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.CourseNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CoursesService {
    private final CoursesRepository coursesRepository;
    private final UsersService usersService;
    private final EnrollmentsCoursesManager enrollmentsCoursesManager;

    public CoursesService(
            final CoursesRepository coursesRepository,
            final UsersService usersService,
            final EnrollmentsCoursesManager enrollmentsCoursesManager
    ) {
        this.coursesRepository = coursesRepository;
        this.usersService = usersService;
        this.enrollmentsCoursesManager = enrollmentsCoursesManager;
    }

    public CoursesEntity createCourse(CreateCourseDTO createCourseDTO) {
        CoursesEntity newCourse = new CoursesEntity();

        this.verifyCreatorUserReachedCourseCreationLimit();

        newCourse.setName(createCourseDTO.name());
        newCourse.setDescription(createCourseDTO.description());
        newCourse.setMaximumAttendees(createCourseDTO.maximumAttendees());
        newCourse.setUsersEntity(this.usersService.getAuthenticatedUser());

        return this.coursesRepository.save(newCourse);
    }

    private void verifyCreatorUserReachedCourseCreationLimit() {
        UsersEntity user = this.usersService.getAuthenticatedUser();

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

        course.setName(updateCourseNameDTO.newCourseName());

        this.coursesRepository.save(course);
    }

    public void updateCourseDescription(UpdateCourseDescriptionDTO updateCourseDescriptionDTO, String courseId) {
        CoursesEntity course = this.getCourseById(courseId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        course.setDescription(updateCourseDescriptionDTO.newCourseDescription());

        this.coursesRepository.save(course);
    }

    private void verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(String courseId) {
        CoursesEntity course = this.getCourseById(courseId);

        UsersEntity user = this.usersService.getAuthenticatedUser();

        if (!Objects.equals(course.getUsersEntity().getId(), user.getId())) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    public List<CourseResponseDTO> getAllCoursesCreatedByCreatorUser() {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        return this.getAllCoursesByCreatorUserId(user.getId()).stream().map(coursesEntity -> {
            List<EnrollmentsEntity> enrollments = this.enrollmentsCoursesManager.getAllEnrollmentsByCourseId(coursesEntity.getId());

            int numberAvailableSpots = Math.max(0, coursesEntity.getMaximumAttendees() - enrollments.size());

            int amountEnrollment = enrollments.size();

            return new CourseResponseDTO(
                    coursesEntity.getId(),
                    coursesEntity.getName(),
                    coursesEntity.getDescription(),
                    coursesEntity.getMaximumAttendees(),
                    numberAvailableSpots,
                    amountEnrollment,
                    coursesEntity.getCreatedAt(),
                    coursesEntity.getUsersEntity().getName()
            );
        }).toList();
    }

    public void deactivateCourse(String courseId) {
        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        this.coursesRepository.deleteById(courseId);
    }

    private List<CoursesEntity> getAllCoursesByDescriptionKeyword(String description_keyword) {
        return this.coursesRepository.findByDescriptionContainingIgnoreCase(description_keyword);
    }

    public List<CourseResponseDTO> getCourses(String description_keyword) {
        return this.getAllCoursesByDescriptionKeyword(description_keyword).stream().map(coursesEntity -> {
            List<EnrollmentsEntity> enrollments = this.enrollmentsCoursesManager.getAllEnrollmentsByCourseId(coursesEntity.getId());

            int numberAvailableSpots = Math.max(0, coursesEntity.getMaximumAttendees() - enrollments.size());

            int amountEnrollment = enrollments.size();

            return new CourseResponseDTO(
                    coursesEntity.getId(),
                    coursesEntity.getName(),
                    coursesEntity.getDescription(),
                    coursesEntity.getMaximumAttendees(),
                    numberAvailableSpots,
                    amountEnrollment,
                    coursesEntity.getCreatedAt(),
                    coursesEntity.getUsersEntity().getName()
            );
        }).toList();
    }

    public List<CoursesSubscribedResponseDTO> getCoursesByUserEnrollments() {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        return this.enrollmentsCoursesManager.getAllEnrollmentsByUserId(user.getId()).stream().map(enrollmentEntity -> new CoursesSubscribedResponseDTO(
                enrollmentEntity.getCoursesEntity().getId(),
                enrollmentEntity.getCoursesEntity().getName(),
                enrollmentEntity.getCoursesEntity().getDescription(),
                enrollmentEntity.getCoursesEntity().getCreatedAt(),
                enrollmentEntity.getCreatedAt(),
                enrollmentEntity.getCoursesEntity().getUsersEntity().getName()
        )).toList();
    }
}
