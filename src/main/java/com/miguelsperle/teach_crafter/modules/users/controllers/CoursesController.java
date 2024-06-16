package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.CustomAccessDeniedHandlerResponseDTO;
import com.miguelsperle.teach_crafter.dtos.general.CustomAuthenticationEntryResponseDTO;
import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.*;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.*;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesContentsService;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Operation(summary = "Create a course", description = "This route is responsible for allowing a creator user to create a course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course created successfully\", \"status\": 201}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Name", description = "Error returned because the name is missing in the request", value = "{\"message\": \"Name is required to create a course\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Description", description = "Error returned because the description is missing in the request", value = "{\"message\": \"Description is required to create a course\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Maximum Attendees", description = "Error returned because the maximum attendees is missing in the request", value = "{\"message\": \"Maximum attendees is required to create a course\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Course Creation Limited Reached", description = "Error returned because the creator user has already reached the maximum limit of 5 course creations", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> createCourse(@RequestBody @Valid CreateCourseDTO createCourseDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.createCourse(createCourseDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Course created successfully", HttpStatus.CREATED.value()));
    }

    @PutMapping("/{courseId}/update-name")
    @Operation(summary = "Update course name", description = "This route is responsible for allowing a creator user to update specific course name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course name updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Course Name", description = "Error returned because the new course name is missing in the request", value = "{\"message\": \"A new course name is required to update the current course name\", \"status\": 400}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to update a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateCourseName(@PathVariable String courseId, @RequestBody @Valid UpdateCourseNameDTO updateCourseNameDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.updateCourseName(updateCourseNameDTO, courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course name updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseId}/update-description")
    @Operation(summary = "Update course description", description = "This route is responsible for allowing a creator user to update specific course description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course description updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Course Description", description = "Error returned because the new course description is missing in the request", value = "{\"message\": \"A new course description is required to update the current course description\", \"status\": 400}"),
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json",schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to update a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateCourseDescription(@PathVariable String courseId, @RequestBody @Valid UpdateCourseDescriptionDTO updateCourseDescriptionDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.updateCourseDescription(updateCourseDescriptionDTO, courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course description updated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/creator-owned")
    @Operation(summary = "Fetch all courses created by the creator user", description = "This route is responsible for allowing a creator user to fetch all courses created by them")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseResponseDTO.class)))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public List<CourseResponseDTO> getAllCoursesCreatedByCreatorUser() {
        return this.coursesService.getAllCoursesCreatedByCreatorUser();
    }

    @DeleteMapping("/{courseId}/deactivate")
    @Operation(summary = "Deactivate a specific course", description = "This route is responsible for allowing a creator user to deactivate a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course successfully deactivated\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to deactivate a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> deactivateCourse(@PathVariable String courseId) {
        this.coursesService.deactivateCourse(courseId);

        return ResponseEntity.ok().body(new MessageResponseDTO("Course successfully deactivated", HttpStatus.OK.value()));
    }

    @GetMapping
    @Operation(summary = "Fetch courses by description keyword", description = "This route is responsible for fetching courses matching the provided description keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseResponseDTO.class)))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public List<CourseResponseDTO> getCourses(@RequestParam String description_keyword) {
        return this.coursesService.getCourses(description_keyword);
    }

    @GetMapping("/subscribed")
    @Operation(summary = "Fetch courses by user enrollments", description = "This route is responsible for fetching courses by enrollments of a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CoursesSubscribedResponseDTO.class)))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}")
                    })),
    })
    @SecurityRequirement(name = "jwt_auth")
    public List<CoursesSubscribedResponseDTO> getCoursesByUserEnrollments() {
        return this.coursesService.getCoursesByUserEnrollments();
    }

    // BELOW IS EVERYTHING RELATED ABOUT COURSE CONTENT

    @PostMapping("/{courseId}/content")
    @Operation(summary = "Create a course content", description = "This route is responsible for allowing a creator user to create a content for a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course content created successfully\", \"status\": 201}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Description", description = "Error returned because the description is missing in the request", value = "{\"message\": \"Description is required to create a content\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Release Date", description = "Error returned because the release date is missing in the request", value = "{\"message\": \"A release date is required to create a content\", \"status\": 400}"),
                            @ExampleObject(name = "Missing Course Module", description = "Error returned because the course module is missing in the request", value = "{\"message\": \"Course module is required to create a content\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid Release Date", description = "Error returned because the release date is in the past", value = "{\"message\": \"Release date cannot be in the past\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to create a content for a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> createCourseContent(@PathVariable String courseId, @RequestBody @Valid CreateCourseContentDTO createCourseContentDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.createCourseContent(courseId, createCourseContentDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Course content created successfully", HttpStatus.CREATED.value()));
    }

    @PostMapping("/{courseContentId}/content/upload-video")
    @Operation(summary = "Upload the course content video", description = "This route is responsible for allowing a creator user to upload a video for a specific course content")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course content video uploaded successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to upload a video for a specific content of a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Content Found", description = "Error returned because there is no course content with the provided ID", value = "{\"message\": \"Course content not found\", \"status\": 404}"),
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> uploadCourseContentVideo(@PathVariable String courseContentId, @RequestPart("videoFile") MultipartFile videoFile) {
        this.coursesContentsService.uploadCourseContentVideo(courseContentId, videoFile);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content video uploaded successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-description")
    @Operation(summary = "Update the course content description", description = "This route is responsible for allowing a creator user to update the description of a specific content")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course content description updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Content Description", description = "Error returned because the new content description is missing in the request", value = "{\"message\": \"A new content description is required to update the current content description\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json",schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to update a specific content of a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Content Found", description = "Error returned because there is no course content with the provided ID", value = "{\"message\": \"Course content not found\", \"status\": 404}"),
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateCourseContentDescription(@PathVariable String courseContentId, @RequestBody @Valid UpdateCourseContentDescriptionDTO updateCourseContentDescriptionDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.updateCourseContentDescription(courseContentId, updateCourseContentDescriptionDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content description updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-video")
    @Operation(summary = "Update the course content video", description = "This route is responsible for allowing a creator user to update the video of a specific content")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course content video updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to update a specific content of a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Content Found", description = "Error returned because there is no course content with the provided ID", value = "{\"message\": \"Course content not found\", \"status\": 404}"),
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateCourseContentVideo(@PathVariable String courseContentId, @RequestPart("videoFile") MultipartFile videoFile) {
        this.coursesContentsService.uploadCourseContentVideo(courseContentId, videoFile);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content video updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-release-date")
    @Operation(summary = "Update the course content release date", description = "This route is responsible for allowing a creator user to update the release date of a specific content")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course content release date updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing New Content Release Date", description = "Error returned because the new content release date is missing in the request", value = "{\"message\": \"A new content release date is required to update the current content release date\", \"status\": 400}"),
                            @ExampleObject(name = "Invalid New Release Date", description = "Error returned because the new release date is in the past", value = "{\"message\": \"Release date cannot be in the past\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to update a specific content of a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Content Found", description = "Error returned because there is no course content with the provided ID", value = "{\"message\": \"Course content not found\", \"status\": 404}"),
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))

    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateCourseContentReleaseDate(@PathVariable String courseContentId, @RequestBody @Valid UpdateCourseContentReleaseDateDTO updateCourseContentReleaseDateDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.updateCourseContentReleaseDate(courseContentId, updateCourseContentReleaseDateDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content release date updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{courseContentId}/content/update-course-module")
    @Operation(summary = "Update the course content module", description = "This route is responsible for allowing a creator user to update the module of a specific content")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Course content module updated successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Missing New Content Module", description = "Error returned because the new content module is missing in the request", value = "{\"message\": \"A new content module is required to update the current content module\", \"status\": 400}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to update a specific content of a course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Content Found", description = "Error returned because there is no course content with the provided ID", value = "{\"message\": \"Course content not found\", \"status\": 404}"),
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> updateCourseContentModule(@PathVariable String courseContentId, @RequestBody @Valid UpdateCourseContentModuleDTO updateCourseContentModuleDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesContentsService.updateCourseContentModule(courseContentId, updateCourseContentModuleDTO);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course content module updated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/{courseId}/contents/creator-owned")
    @Operation(summary = "Fetch all content of a specific course created by the creator user", description = "This route is responsible for allowing a creator user to fetch all content of a specific course created by them")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseContentResponseDTO.class)))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class, CustomAccessDeniedHandlerResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Creator User Not Course Owner", description = "Error returned because the creator user tried to fetch all contents of a specific course that is not theirs", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}"),
                            @ExampleObject(name = "Creator Role Required", description = "Error returned because the user does not have the 'creator' role required to access this resource", value = "{\"message\": \"Access to this resource is restricted\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Course Found", description = "Error returned because there is no course with the provided ID", value = "{\"message\": \"Course not found\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public List<CourseContentResponseDTO> getCourseContentsCreatedByCreatorUser(@PathVariable String courseId) {
        return this.coursesContentsService.getCourseContentsCreatedByCreatorUser(courseId);
    }

    @GetMapping("/{courseId}/contents/subscribed")
    @Operation(summary = "Fetch all published content of a specific course for a subscribed user", description = "This route is responsible for allowing a subscribed user to fetch all published content of a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseContentResponseDTO.class)))),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Enrollment Found", description = "Error returned because there is no enrollment with the provided course id", value = "{\"message\": \"Enrollment does not exist\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public List<CourseContentResponseDTO> getPublishedCourseContentsForSubscribedUser(@PathVariable String courseId) {
        return this.coursesContentsService.getPublishedCourseContentsForSubscribedUser(courseId);
    }
}
