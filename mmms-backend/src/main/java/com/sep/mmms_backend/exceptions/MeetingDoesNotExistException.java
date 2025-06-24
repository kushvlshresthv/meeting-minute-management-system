package com.sep.mmms_backend.exceptions;


public class MeetingDoesNotExistException extends RuntimeException{
    public MeetingDoesNotExistException(String message) {
        super(message);
    }

    public MeetingDoesNotExistException(ExceptionMessages message) {
        super(message.toString());
    }
}
