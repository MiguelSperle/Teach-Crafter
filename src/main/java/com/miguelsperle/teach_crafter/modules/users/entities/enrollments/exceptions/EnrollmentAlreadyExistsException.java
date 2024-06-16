package com.miguelsperle.teach_crafter.modules.users.entities.enrollments.exceptions;

public class EnrollmentAlreadyExistsException extends RuntimeException {
    public EnrollmentAlreadyExistsException(String message){
        super(message);
    }
}
