package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.services.CoursesService;
import com.miguelsperle.teach_crafter.modules.users.services.RequestFieldValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
