package com.miguelsperle.teach_crafter.modules.users.entities.passwordResetToken.exceptions;

public class PasswordResetTokenRecoveryIsNotExpiredException extends RuntimeException {
    public PasswordResetTokenRecoveryIsNotExpiredException(String message){
        super(message);
    }
}
