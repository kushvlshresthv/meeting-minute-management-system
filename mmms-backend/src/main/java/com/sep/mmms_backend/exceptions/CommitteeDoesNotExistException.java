package com.sep.mmms_backend.exceptions;

import org.springframework.validation.Errors;

public class CommitteeDoesNotExistException extends RuntimeException {

    int committeeId;
    public CommitteeDoesNotExistException(ExceptionMessages message, int committeeId) {
        super(message.toString());
        this.committeeId = committeeId;
    }

    public int getCommitteeId() {
        return committeeId;
    }
}
