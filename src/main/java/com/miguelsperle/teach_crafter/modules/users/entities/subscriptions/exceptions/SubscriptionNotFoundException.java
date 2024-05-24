package com.miguelsperle.teach_crafter.modules.users.entities.subscriptions.exceptions;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}
