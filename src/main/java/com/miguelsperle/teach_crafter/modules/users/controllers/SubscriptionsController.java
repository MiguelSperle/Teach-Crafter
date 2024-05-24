package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.services.SubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
public class SubscriptionsController {
    @Autowired
    private SubscriptionsService subscriptionsService;

    @PostMapping("/create/{courseId}")
    public ResponseEntity<Object> createCourseSubscription(@PathVariable String courseId) {
        this.subscriptionsService.createCourseSubscription(courseId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Subscription created successfully", HttpStatus.CREATED.value()));
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<Object> deleteCourseSubscription(@PathVariable String courseId) {
        this.subscriptionsService.deleteCourseSubscription(courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Subscription deleted successfully", HttpStatus.OK.value()));
    }
}
