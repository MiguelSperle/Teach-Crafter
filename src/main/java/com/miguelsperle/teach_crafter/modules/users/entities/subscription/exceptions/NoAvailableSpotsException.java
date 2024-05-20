package com.miguelsperle.teach_crafter.modules.users.entities.subscription.exceptions;

public class NoAvailableSpotsException extends RuntimeException {
    public NoAvailableSpotsException(String message){
        super(message);
    }
}
