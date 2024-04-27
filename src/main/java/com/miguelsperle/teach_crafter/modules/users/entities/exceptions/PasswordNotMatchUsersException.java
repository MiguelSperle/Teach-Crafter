package com.miguelsperle.teach_crafter.modules.users.entities.exceptions;

public class PasswordNotMatchUsersException extends RuntimeException {
    public PasswordNotMatchUsersException(String message){
        super(message);
    }
}
