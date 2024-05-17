package com.miguelsperle.teach_crafter.modules.users.entities.courses.exceptions;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(String message){
        super(message);
    }
}
