package com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message){
        super(message);
    }
}
