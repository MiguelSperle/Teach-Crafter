package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.dtos.general.MessageResponseDTO;
import com.miguelsperle.teach_crafter.modules.users.services.SubscriptionsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
public class SubscriptionsController {
    private final SubscriptionsService subscriptionsService;

    public SubscriptionsController(final SubscriptionsService subscriptionsService) {
        this.subscriptionsService = subscriptionsService;
    }

    @PostMapping("/{courseId}/create")
    public ResponseEntity<Object> createCourseSubscription(@PathVariable String courseId) {
        this.subscriptionsService.createCourseSubscription(courseId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Subscription created successfully", HttpStatus.CREATED.value()));
    }

    @DeleteMapping("/{courseId}/delete")
    public ResponseEntity<Object> deleteCourseSubscription(@PathVariable String courseId) {
        this.subscriptionsService.deleteCourseSubscription(courseId);

        return ResponseEntity.ok()
                .body(new MessageResponseDTO("Subscription deleted successfully", HttpStatus.OK.value()));
    }
}
