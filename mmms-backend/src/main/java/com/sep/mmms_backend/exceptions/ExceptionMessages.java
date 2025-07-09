package com.sep.mmms_backend.exceptions;


public enum ExceptionMessages {
    USERNAME_ALREADY_EXISTS("User with the username already exists"),
    MEETING_ALREADY_EXISTS("Meeting with the meeting already exists"),
    MEETING_DOES_NOT_EXIST("Meeting that is to be updated does not exist"),
    USER_DOES_NOT_EXIST("User does not exist"),
    USER_CAN_ONLY_UPDATE_THEIR_OWN_DATA("A user can only update their own data"),
    ROUTE_UPDATE_USER_CANT_UPDATE_PASSWORD("This route can't be used to update password"),
    USER_VALIDATION_FALIED("User data does not meet validation criterias"),
    MEMBER_DOES_NOT_EXIST("The specified member does not exist"),
    INVALID_MEMBERSHIP("Invalid membership"),
    VALIDATION_FAILED("Validation Failed"),
    COMMITTEE_DOES_NOT_EXIST("The specified committee does not exist"),
    NO_VALID_MEMBERSHIP("No valid membership"),
    COMMITTEE_NOT_ACCESSIBLE("Specified committee is not accessible"),
    MEMBER_NOT_IN_COMMITTEE("The specified member is not associated with the committee"),
    ;


    final private String message;
    ExceptionMessages(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
