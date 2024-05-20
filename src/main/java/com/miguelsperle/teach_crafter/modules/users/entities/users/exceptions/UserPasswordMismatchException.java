package com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions;

public class UserPasswordMismatchException extends RuntimeException {
    public UserPasswordMismatchException(String message){
        super(message);
    }
}
