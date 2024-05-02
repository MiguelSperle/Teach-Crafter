package com.miguelsperle.teach_crafter.modules.users.entities.users.exceptions;

public class PasswordNotMatchUserException extends RuntimeException {
    public PasswordNotMatchUserException(String message){
        super(message);
    }
}
