package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.*;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.CourseContentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.InvalidReleaseDateException;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions.EnrollmentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesContentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Service
public class CoursesContentsService {
    private final CoursesContentsRepository coursesContentsRepository;
    private final CoursesService coursesService;
    private final UsersService usersService;
    private final EnrollmentsService enrollmentsService;
    private final CloudinaryVideoService cloudinaryVideoService;

    public CoursesContentsService(
            final CoursesContentsRepository coursesContentsRepository,
            final CoursesService coursesService,
            final UsersService usersService,
            final EnrollmentsService enrollmentsService,
            final CloudinaryVideoService cloudinaryVideoService
    ) {
        this.coursesContentsRepository = coursesContentsRepository;
        this.coursesService = coursesService;
        this.usersService = usersService;
        this.enrollmentsService = enrollmentsService;
        this.cloudinaryVideoService = cloudinaryVideoService;
    }

    public CoursesContentsEntity createCourseContent(String courseId, CreateCourseContentDTO createCourseContentDTO) {
        CoursesContentsEntity newCourseContent = new CoursesContentsEntity();

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        String courseStatus = this.isReleaseDateValid(createCourseContentDTO.releaseDate());

        newCourseContent.setDescription(createCourseContentDTO.description());
        newCourseContent.setStatus(courseStatus);
        newCourseContent.setReleaseDate(createCourseContentDTO.releaseDate());
        newCourseContent.setCoursesEntity(this.coursesService.getCourseById(courseId));
        newCourseContent.setContentModule(createCourseContentDTO.courseModule());

        return this.coursesContentsRepository.save(newCourseContent);
    }

    public String isReleaseDateValid(LocalDate releaseDate) {
        LocalDate currentDate = LocalDate.now();

        if (releaseDate.isBefore(currentDate)) {
            throw new InvalidReleaseDateException("Release date cannot be in the past");
        }

        return releaseDate.equals(currentDate) ? "PUBLISHED" : "PENDING";
    }


    private void verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(String courseId) {
        CoursesEntity course = this.coursesService.getCourseById(courseId);

        UsersEntity user = this.usersService.getAuthenticatedUser();

        if (!Objects.equals(course.getUsersEntity().getId(), user.getId())) {
            throw new TaskDeniedException("Task not allowed");
        }
    }

    private CoursesContentsEntity getCourseContentById(String courseContentId) {
        return this.coursesContentsRepository.findById(courseContentId)
                .orElseThrow(() -> new CourseContentNotFoundException("Course content not found"));
    }

    public void uploadCourseContentVideo(String courseContentId, MultipartFile videoFile) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setVideoUrl(this.cloudinaryVideoService.uploadVideoFile(videoFile, "course_videos"));

        this.coursesContentsRepository.save(courseContent);
    }

    public void updateCourseContentDescription(String courseContentId, UpdateCourseContentDescriptionDTO updateCourseContentDescriptionDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setDescription(updateCourseContentDescriptionDTO.newContentDescription());

        this.coursesContentsRepository.save(courseContent);
    }

    public void updateCourseContentReleaseDate(String courseContentId, UpdateCourseContentReleaseDateDTO updateCourseContentReleaseDateDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        String courseStatus = this.isReleaseDateValid(updateCourseContentReleaseDateDTO.newContentReleaseDate());

        courseContent.setStatus(courseStatus);
        courseContent.setReleaseDate(updateCourseContentReleaseDateDTO.newContentReleaseDate());

        this.coursesContentsRepository.save(courseContent);
    }

    public void updateCourseContentModule(String courseContentId, UpdateCourseContentModuleDTO updateCourseContentModuleDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setContentModule(updateCourseContentModuleDTO.newContentModule());

        this.coursesContentsRepository.save(courseContent);
    }

    public List<CourseContentResponseDTO> getCourseContentsCreatedByCreatorUser(String courseId) {
        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseId);

        return this.getAllCourseContentsByCourseId(courseId).stream().map(coursesContentsEntity ->
                new CourseContentResponseDTO(
                        coursesContentsEntity.getId(),
                        coursesContentsEntity.getDescription(),
                        coursesContentsEntity.getVideoUrl(),
                        coursesContentsEntity.getStatus(),
                        coursesContentsEntity.getReleaseDate(),
                        coursesContentsEntity.getContentModule(),
                        coursesContentsEntity.getCreatedAt()
                )).toList();
    }

    private List<CoursesContentsEntity> getAllCourseContentsByCourseId(String courseId) {
        return this.coursesContentsRepository.findAllByCoursesEntityId(courseId);
    }

    public void ensureUserIsSubscribed(String userId, String courseId) {
        this.enrollmentsService.getEnrollmentByUserIdAndCourseId(userId, courseId).orElseThrow(() -> new EnrollmentNotFoundException("Enrollment does not exist"));
    }

    public List<CourseContentResponseDTO> getPublishedContentsForSubscribedUser(String courseId) {
        UsersEntity user = this.usersService.getAuthenticatedUser();

        this.ensureUserIsSubscribed(user.getId(), courseId);

        return this.getAllPublishedContentsByCourseIdAndStatus(courseId).stream().map(coursesContentsEntity ->
                new CourseContentResponseDTO(
                        coursesContentsEntity.getId(),
                        coursesContentsEntity.getDescription(),
                        coursesContentsEntity.getVideoUrl(),
                        coursesContentsEntity.getStatus(),
                        coursesContentsEntity.getReleaseDate(),
                        coursesContentsEntity.getContentModule(),
                        coursesContentsEntity.getCreatedAt()
                )).toList();
    }

    private List<CoursesContentsEntity> getAllPublishedContentsByCourseIdAndStatus(String courseId) {
        return this.coursesContentsRepository.findAllByCoursesEntityIdAndStatus(courseId, "PUBLISHED");
    }

    public List<CoursesContentsEntity> saveAllCoursesContents(List<CoursesContentsEntity> coursesContents) {
        return this.coursesContentsRepository.saveAll(coursesContents);
    }

    public List<CoursesContentsEntity> getAllCoursesContentsByPendingStatus(String pendingStatus) {
        return this.coursesContentsRepository.findAllByStatus(pendingStatus);
    }
}
