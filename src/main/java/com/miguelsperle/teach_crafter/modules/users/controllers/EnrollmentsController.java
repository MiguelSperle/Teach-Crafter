package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.CustomAuthenticationEntryResponseDTO;
import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.services.EnrollmentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollment")
public class EnrollmentsController {
    private final EnrollmentsService enrollmentsService;

    public EnrollmentsController(final EnrollmentsService enrollmentsService) {
        this.enrollmentsService = enrollmentsService;
    }

    @PostMapping("/{courseId}/create")
    @Operation(summary = "Subscribe to a course", description = "This route is responsible for allowing a user to subscribe to a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Enrollment created successfully\", \"status\": 201}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {MessageResponseDTO.class, CustomAuthenticationEntryResponseDTO.class}),
                    examples = {
                            @ExampleObject(name = "Owner Self-Enrollment", description = "Error returned because the authenticated user is the owner of the course and cannot subscribe in the course itself", value = "{\"message\": \"Task not allowed\", \"status\": 403}"),
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Available Spots", description = "Error returned because authenticated user tried to subscribe in the course, but there are no available spots", value = "{\"message\": \"No available spots\", \"status\": 409}"),
                            @ExampleObject(name = "Enrollment Already Exists", description = "Error returned because authenticated user tried to subscribe in the course, but was already subscribed", value = "{\"message\": \"You have already subscribed in this course\", \"status\": 409}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> createCourseEnrollment(@PathVariable String courseId) {
        this.enrollmentsService.createCourseEnrollment(courseId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Enrollment created successfully", HttpStatus.CREATED.value()));
    }

    @DeleteMapping("/{courseId}/delete")
    @Operation(summary = "Unsubscribe from a course", description = "This route is responsible for allowing a user to unsubscribe from a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(value = "{\"message\": \"Enrollment deleted successfully\", \"status\": 200}")
                    })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomAuthenticationEntryResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Missing Authorization Token", description = "Error returned because authorization token is missing in the request header", value = "{\"message\": \"Authorization token missing in request header\", \"status\": 403}")
                    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "No Enrollment Found", description = "Error returned because authenticated user tried to unsubscribe from a specific course, but there is no Enrollment", value = "{\"message\": \"Enrollment does not exist\", \"status\": 404}")
                    }))
    })
    @SecurityRequirement(name = "jwt_auth")
    public ResponseEntity<Object> deleteCourseEnrollment(@PathVariable String courseId) {
        this.enrollmentsService.deleteCourseEnrollment(courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Enrollment deleted successfully", HttpStatus.OK.value()));
    }
}
