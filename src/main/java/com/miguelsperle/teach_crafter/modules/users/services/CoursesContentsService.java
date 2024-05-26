package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadImageModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadVideoModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.CreateCourseContentDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentDescription;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.CourseContentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.InvalidReleaseDateException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesContentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;


@Service
public class CoursesContentsService {
    @Autowired
    private CoursesContentsRepository coursesContentsRepository;
    @Autowired
    private CoursesService coursesService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private CloudinaryVideoService cloudinaryVideoService;

    public void createCourseContent(String courseId, CreateCourseContentDTO createCourseContentDTO) {
        CoursesContentsEntity newCourseContent = new CoursesContentsEntity();

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        String courseStatus = this.isReleaseDateValid(createCourseContentDTO);

        newCourseContent.setDescription(createCourseContentDTO.description());
        newCourseContent.setStatus(courseStatus);
        newCourseContent.setReleaseDate(createCourseContentDTO.releaseDate());
        newCourseContent.setCoursesEntity(this.coursesService.getCourseById(courseId));
        newCourseContent.setCourseModule(createCourseContentDTO.courseModule());

        this.coursesContentsRepository.save(newCourseContent);
    }

    private String isReleaseDateValid(CreateCourseContentDTO createCourseContentDTO) {
        LocalDate currentDate = LocalDate.now();

        if (createCourseContentDTO.releaseDate().isBefore(currentDate)) {
            throw new InvalidReleaseDateException("Release date cannot be in the past");
        }

        return createCourseContentDTO.releaseDate().equals(currentDate) ? "PUBLISHED" : "PENDING";
    }


    private void verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(String courseId) {
        CoursesEntity course = this.coursesService.getCourseById(courseId);

        UsersEntity user = this.usersService.getUserAuthenticated();

        if (!Objects.equals(course.getUsersEntity().getId(), user.getId())) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    private CoursesContentsEntity getCourseContentById(String courseContentId) {
        return this.coursesContentsRepository.findById(courseContentId)
                .orElseThrow(() -> new CourseContentNotFoundException("Course content not found"));
    }

    public void uploadCourseContentVideo(String courseContentId, UploadVideoModelDTO uploadVideoModelDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setVideoUrl(this.cloudinaryVideoService.uploadVideoFile(uploadVideoModelDTO.videoFile(), "course_videos"));

        this.coursesContentsRepository.save(courseContent);
    }

    public void updateCourseContentDescription(String courseContentId, UpdateCourseContentDescription updateCourseContentDescription){
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setDescription(updateCourseContentDescription.newDescription());

        this.coursesContentsRepository.save(courseContent);
    }
}
