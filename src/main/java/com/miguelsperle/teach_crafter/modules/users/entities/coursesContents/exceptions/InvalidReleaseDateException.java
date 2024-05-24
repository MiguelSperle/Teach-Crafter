package com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.exceptions;

public class InvalidReleaseDateException extends RuntimeException {
    public InvalidReleaseDateException(String message){
        super(message);
    }
}
