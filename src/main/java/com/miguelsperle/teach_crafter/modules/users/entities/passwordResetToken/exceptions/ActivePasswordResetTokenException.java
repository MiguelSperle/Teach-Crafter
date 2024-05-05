package com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions;

public class ActivePasswordResetTokenException extends RuntimeException {
    public ActivePasswordResetTokenException(String message){
        super(message);
    }
}
