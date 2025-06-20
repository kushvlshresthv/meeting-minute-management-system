package com.sep.mmms_backend.response;

public enum ResponseMessage {
    LOGIN_SUCCESSFUL("Login successful"),
    LOGOUT_SUCCESSFUL("Logout successful"),
    AUTHENTICATION_FAILED("Authentication Failed"),
    ACCESS_DENIED("Access Denied"),
    REGISTER_USER_FAILED("Failed to register user"),
    REGISTER_USER_SUCCESS("User registered successfully");



    private final String message;
    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
