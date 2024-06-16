package com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
