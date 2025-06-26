package com.sep.mmms_backend.response;

public enum ResponseMessages {
    LOGIN_SUCCESSFUL("Login successful"),
    LOGOUT_SUCCESSFUL("Logout successful"),
    AUTHENTICATION_FAILED("Authentication Failed"),
    ACCESS_DENIED("Access Denied"),

    //USER RELATED:
    USER_REGISTER_FAILED("Failed to register user"),
    USER_REGISTER_SUCCESS("User registered successfully"),
    USER_UPDATION_FAILED("Failed to update user"),
    USER_UPDATION_SUCCESS("User updated successfully"),
    USER_CAN_ONLY_UPDATE_THEIR_OWN_DATA("A user can only update their own data"),


    //MEETING RELATED:
    MEETING_CREATION_FAILED("Failed to create meeting"),
    MEETING_CREATION_SUCCESSFUL("Meeting registered successfully"),


    //ROUTES MISUSED:
    ROUTE_CREATE_MEETING_MISUSED("This route can only create new meetings, and the meeting_id field shouldn't be populated"),
    ROUTE_UPDATE_USER_MISUSED("Valid uid field is required as this route can only be used to update existing users"),
    ROUTE_UPDATE_USER_CANT_UPDATE_PASSWORD("This route can't be used to update password"),
    ROUTE_REGISTER_MISUED("This route can't be used to update an existing user"),

    ;


    private final String message;
    ResponseMessages(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
