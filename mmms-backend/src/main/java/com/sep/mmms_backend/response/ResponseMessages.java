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

    //MEETING RELATED:
    MEETING_CREATION_FAILED("Failed to create meeting"),
    MEETING_CREATION_SUCCESSFUL("Meeting registered successfully"),
    MEETING_ATTENDEE_ADDITION_SUCCESS("New attendees added to the meeting successfully"),
    MEETING_DETAIL_RETRIEVED_SUCCESSFULLY("Meeting retrieved successfully"),


    //ROUTES MISUSED:
    ROUTE_CREATE_MEETING_MISUSED("This route can only create new meetings, and the meeting_id field shouldn't be populated"),
    ROUTE_UPDATE_USER_MISUSED("Valid uid field is required as this route can only be used to update existing users"),
    ROUTE_REGISTER_MISUED("This route can't be used to update an existing user"),

    //COMMITTEE RELATED:
    COMMITTEE_CREATION_SUCCESS("Committee created successfully"),
    COMMITTEE_MEMBER_ADDITION_SUCCESS("New members added to the committee successfully"),
    COMMITTEES_RETRIEVED_SUCCESSFULLY("Committees retrieved successfully"),

    //MEMBER RELATED:
    MEMBER_CREATION_SUCCESS("Member created successfully"),
    MEMBER_DETAIL_RETRIEVED_SUCCESSFULLY("Member retrieved successfully"),

    HTTP_MESSAGE_NOT_READABLE("HTTP message is not readable"),
    ;


    private final String message;
    ResponseMessages(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
