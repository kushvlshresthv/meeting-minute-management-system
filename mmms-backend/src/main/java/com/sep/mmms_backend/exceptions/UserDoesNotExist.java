package com.sep.mmms_backend.exceptions;

public class UserDoesNotExist extends RuntimeException{
    public UserDoesNotExist(String message) {
        super(message);
    }

    public UserDoesNotExist(ExceptionMessages message) {
        super(message.toString());
    }

    public UserDoesNotExist() {
        super();
    }
}
