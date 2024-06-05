package com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions;

public class SubscriptionAlreadyExistsException extends RuntimeException {
    public SubscriptionAlreadyExistsException(String message){
        super(message);
    }
}
