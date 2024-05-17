package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CourseResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseNameDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions.CourseNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoursesService {
    private final CoursesRepository coursesRepository;
    private final UsersService usersService;

    public void createCourse(CreateCourseDTO createCourseDTO) {
        CoursesEntity newCourse = new CoursesEntity();

        this.verifyCreatorUserHasReachedCourseCreationLimit();

        newCourse.setName(createCourseDTO.name());
        newCourse.setDescription(createCourseDTO.description());
        newCourse.setMaximumAttendees(createCourseDTO.maximumAttendees());
        newCourse.setUsersEntity(this.usersService.getUserAuthenticated());

        this.coursesRepository.save(newCourse);
    }

    private void verifyCreatorUserHasReachedCourseCreationLimit() {
        UsersEntity userLogged = this.usersService.getUserAuthenticated();

        List<CoursesEntity> courses = this.getAllCoursesByCreatorUserId(userLogged.getId());

        if (courses.size() == 5) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    private List<CoursesEntity> getAllCoursesByCreatorUserId(String userId) {
        return this.coursesRepository.findAllByUsersEntityId(userId);
    }

    private CoursesEntity getCourseById(String courseId) {
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

        UsersEntity userLogged = this.usersService.getUserAuthenticated();

        if (!Objects.equals(course.getUsersEntity().getId(), userLogged.getId())) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    public List<CourseResponseDTO> getAllCoursesCreatedByCreatorUser() {
        UsersEntity userLogged = this.usersService.getUserAuthenticated();

        return this.getAllCoursesByCreatorUserId(userLogged.getId()).stream().map(coursesEntity -> new CourseResponseDTO(
                coursesEntity.getId(),
                coursesEntity.getName(),
                coursesEntity.getDescription(),
                coursesEntity.getMaximumAttendees(),
                coursesEntity.getCreatedAt(),
                coursesEntity.getUsersEntity().getName()
        )).toList();
    }
}
