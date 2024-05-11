package com.miguelsperle.teach_crafter.exceptions.general;

public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String message){
        super(message);
    }
}
