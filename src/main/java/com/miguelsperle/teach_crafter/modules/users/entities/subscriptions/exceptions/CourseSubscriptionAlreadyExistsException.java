package com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions;

public class CourseSubscriptionAlreadyExistsException extends RuntimeException {
    public CourseSubscriptionAlreadyExistsException(String message){
        super(message);
    }
}
