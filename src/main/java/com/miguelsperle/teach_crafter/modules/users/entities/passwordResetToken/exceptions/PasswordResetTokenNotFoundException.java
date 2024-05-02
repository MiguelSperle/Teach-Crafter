package com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException(String message){
        super(message);
    }
}
