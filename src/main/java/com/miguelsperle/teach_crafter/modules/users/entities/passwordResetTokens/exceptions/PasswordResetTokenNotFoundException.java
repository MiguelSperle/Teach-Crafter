package com.miguelsperle.teach_crafter.modules.users.entities.passwordResetTokens.exceptions;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException(String message){
        super(message);
    }
}
