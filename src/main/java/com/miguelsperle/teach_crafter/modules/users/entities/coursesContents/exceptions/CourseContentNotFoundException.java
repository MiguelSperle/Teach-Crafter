package com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions;

public class CourseContentNotFoundException extends RuntimeException {
    public CourseContentNotFoundException(String message) {
        super(message);
    }
}
