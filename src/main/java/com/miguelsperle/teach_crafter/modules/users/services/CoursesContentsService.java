package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.exceptions.general.TaskDeniedException;
import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadVideoModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.*;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.CourseContentNotFoundException;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions.InvalidReleaseDateException;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesContentsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Service
public class CoursesContentsService {
    private final CoursesContentsRepository coursesContentsRepository;
    private final CoursesService coursesService;
    private final UsersService usersService;
    private final SubscriptionsService subscriptionsService;
    private final CloudinaryVideoService cloudinaryVideoService;

    public CoursesContentsService(
            final CoursesContentsRepository coursesContentsRepository,
            final CoursesService coursesService,
            final UsersService usersService,
            final SubscriptionsService subscriptionsService,
            final CloudinaryVideoService cloudinaryVideoService
    ) {
        this.coursesContentsRepository = coursesContentsRepository;
        this.coursesService = coursesService;
        this.usersService = usersService;
        this.subscriptionsService = subscriptionsService;
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
        newCourseContent.setCourseModule(createCourseContentDTO.courseModule());

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

    public void updateCourseContentDescription(String courseContentId, UpdateCourseContentDescriptionDTO updateCourseContentDescriptionDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setDescription(updateCourseContentDescriptionDTO.newDescription());

        this.coursesContentsRepository.save(courseContent);
    }

    public void updateCourseContentReleaseDate(String courseContentId, UpdateCourseContentReleaseDateDTO updateCourseContentReleaseDateDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        String courseStatus = this.isReleaseDateValid(updateCourseContentReleaseDateDTO.newReleaseDate());

        courseContent.setStatus(courseStatus);
        courseContent.setReleaseDate(updateCourseContentReleaseDateDTO.newReleaseDate());

        this.coursesContentsRepository.save(courseContent);
    }

    public void updateCourseContentModule(String courseContentId, UpdateCourseContentModuleDTO updateCourseContentModuleDTO) {
        CoursesContentsEntity courseContent = this.getCourseContentById(courseContentId);

        this.verifyCreatorUserIdAuthenticatedMatchesCourseOwnerId(courseContent.getCoursesEntity().getId());

        courseContent.setCourseModule(updateCourseContentModuleDTO.newCourseModule());

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
                        coursesContentsEntity.getCourseModule(),
                        coursesContentsEntity.getCreatedAt()
                )).toList();
    }

    private List<CoursesContentsEntity> getAllCourseContentsByCourseId(String courseId) {
        return this.coursesContentsRepository.findAllByCoursesEntityId(courseId);
    }

    public List<CourseContentResponseDTO> getCourseContentsWhetherUserIsSubscribedInTheCourse(String courseId) {
        UsersEntity user = this.usersService.getUserAuthenticated();

        this.subscriptionsService.ensureUserIsNotSubscribed(user.getId(), courseId);

        return this.getAllPublishedCourseContentsByCourseIdAndStatus(courseId).stream().map(coursesContentsEntity ->
                new CourseContentResponseDTO(
                        coursesContentsEntity.getId(),
                        coursesContentsEntity.getDescription(),
                        coursesContentsEntity.getVideoUrl(),
                        coursesContentsEntity.getStatus(),
                        coursesContentsEntity.getReleaseDate(),
                        coursesContentsEntity.getCourseModule(),
                        coursesContentsEntity.getCreatedAt()
                )).toList();
    }

    private List<CoursesContentsEntity> getAllPublishedCourseContentsByCourseIdAndStatus(String courseId) {
        return this.coursesContentsRepository.findAllByCoursesEntityIdAndStatus(courseId, "PUBLISHED");
    }

    public void saveAllCoursesContents(List<CoursesContentsEntity> coursesContents) {
        this.coursesContentsRepository.saveAll(coursesContents);
    }

    public List<CoursesContentsEntity> getAllCoursesContentsByPendingStatus(String pendingStatus) {
        return this.coursesContentsRepository.findAllByStatus(pendingStatus);
    }
}
