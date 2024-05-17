package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CourseResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseNameDTO;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CoursesController {
    private final CoursesService coursesService;
    private final RequestFieldValidationService requestFieldValidationService;

    @PostMapping("/create")
    public ResponseEntity<Object> createCourse(@RequestBody @Valid CreateCourseDTO createCourseDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.createCourse(createCourseDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Course created successfully", HttpStatus.CREATED.value()));
    }

    @PutMapping("/update-name/{courseId}")
    public ResponseEntity<Object> updateCourseName(@PathVariable String courseId, @RequestBody @Valid UpdateCourseNameDTO updateCourseNameDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.updateCourseName(updateCourseNameDTO, courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course name updated successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/update-description/{courseId}")
    public ResponseEntity<Object> updateCourseDescription(@PathVariable String courseId, @RequestBody @Valid UpdateCourseDescriptionDTO updateCourseDescriptionDTO, BindingResult bindingResult) {
        this.requestFieldValidationService.validationErrors(bindingResult);

        this.coursesService.updateCourseDescription(updateCourseDescriptionDTO, courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Course description updated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/creator-owned")
    public List<CourseResponseDTO> getAllCoursesCreatedByCreatorUser(){
        return this.coursesService.getAllCoursesCreatedByCreatorUser();
    }
}
