package com.miguelsperle.teach_crafter.exceptions.general;

public class TaskDeniedException extends RuntimeException  {
    public TaskDeniedException(String message){
        super(message);
    }
}
