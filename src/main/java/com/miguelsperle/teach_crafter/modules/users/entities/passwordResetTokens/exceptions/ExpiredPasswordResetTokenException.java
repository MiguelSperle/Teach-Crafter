package com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions;

public class ExpiredPasswordResetTokenException extends RuntimeException {
    public ExpiredPasswordResetTokenException(String message){
        super(message);
    }
}
