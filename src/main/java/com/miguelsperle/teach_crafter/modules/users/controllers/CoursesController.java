package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary.UploadVideoModelDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.*;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.*;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesContentsService;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CoursesController {
    private final CoursesService coursesService;
    private final RequestFieldValidationService requestFieldValidationService;
    private final CoursesContentsService coursesContentsService;

    public CoursesController(
            final CoursesService coursesService,
            final RequestFieldValidationService requestFieldValidationService,
            final CoursesContentsService coursesContentsService
    ) {
        this.coursesService = coursesService;
        this.requestFieldValidationService = requestFieldValidationService;
        this.coursesContentsService = coursesContentsService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createCourse(@RequestBody @Valid CreateCourseDTO createCourseDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.createCourse(createCourseDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Course created successfully", HttpStatus.CREATED.value()));
    }

    @PutMapping("/{courseId}/update-name")
    public ResponseEntity<Object> updateCourseName(@PathVariable String courseId, @RequestBody @Valid UpdateCourseNameDTO updateCourseNameDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.updateCourseName(updateCourseNameDTO, courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course name updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseId}/update-description")
    public ResponseEntity<Object> updateCourseDescription(@PathVariable String courseId, @RequestBody @Valid UpdateCourseDescriptionDTO updateCourseDescriptionDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.updateCourseDescription(updateCourseDescriptionDTO, courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course description updated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/creator-owned")
    public List<CourseResponseDTO> getAllCoursesCreatedByCreatorUser() {
        return this.coursesService.getAllCoursesCreatedByCreatorUser();
    }

    @DeleteMapping("/{courseId}/deactivate")
    public ResponseEntity<Object> deactivateCourse(@PathVariable String courseId) {
        this.coursesService.deactivateCourse(courseId);

        return ResponseEntity.ok().body(new MessageResponseDTO("Course successfully deactivated", HttpStatus.OK.value()));
    }

    @GetMapping
    public List<CourseResponseDTO> getCourses(@RequestParam String description_keyword) {
        return this.coursesService.getCourses(description_keyword);
    }

    @GetMapping("/subscribed")
    public List<CoursesSubscribedResponseDTO> getCoursesByUserSubscriptions() {
        return this.coursesService.getCoursesByUserSubscriptions();
    }

    // BELOW IS EVERYTHING RELATED ABOUT COURSE CONTENT

    @PostMapping("/{courseId}/content")
    public ResponseEntity<Object> createCourseContent(@PathVariable String courseId, @RequestBody @Valid CreateCourseContentDTO createCourseContentDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.createCourseContent(courseId, createCourseContentDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Course content created successfully", HttpStatus.CREATED.value()));
    }

    @PostMapping("/{courseContentId}/content/upload-video")
    public ResponseEntity<Object> uploadCourseContentVideo(@PathVariable String courseContentId, @Valid UploadVideoModelDTO uploadVideoModelDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.uploadCourseContentVideo(courseContentId, uploadVideoModelDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Course content video uploaded successfully ", HttpStatus.CREATED.value()));
    }

    @PutMapping("/{courseContentId}/content/update-description")
    public ResponseEntity<Object> updateCourseContentDescription(@PathVariable String courseContentId, @RequestBody @Valid UpdateCourseContentDescriptionDTO updateCourseContentDescriptionDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.updateCourseContentDescription(courseContentId, updateCourseContentDescriptionDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content description updated successfully ", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-video")
    public ResponseEntity<Object> updateCourseContentVideo(@PathVariable String courseContentId, @Valid UploadVideoModelDTO uploadVideoModelDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.uploadCourseContentVideo(courseContentId, uploadVideoModelDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content video updated successfully ", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-release-date")
    public ResponseEntity<Object> updateCourseContentReleaseDate(@PathVariable String courseContentId, @RequestBody @Valid UpdateCourseContentReleaseDateDTO updateCourseContentReleaseDateDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.updateCourseContentReleaseDate(courseContentId, updateCourseContentReleaseDateDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content release date updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-course-module")
    public ResponseEntity<Object> updateCourseContentModule(@PathVariable String courseContentId, @RequestBody @Valid UpdateCourseContentModuleDTO updateCourseContentModuleDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.updateCourseContentModule(courseContentId, updateCourseContentModuleDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content module updated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/{courseId}/contents/creator-owned")
    public List<CourseContentResponseDTO> getCourseContentsCreatedByCreatorUser(@PathVariable String courseId) {
        return this.coursesContentsService.getCourseContentsCreatedByCreatorUser(courseId);
    }

    @GetMapping("/{courseId}/contents/subscribed")
    public List<CourseContentResponseDTO> getCourseContentsWhetherUserIsSubscribedInTheCourse(@PathVariable String courseId) {
        return this.coursesContentsService.getCourseContentsWhetherUserIsSubscribedInTheCourse(courseId);
    }
}
