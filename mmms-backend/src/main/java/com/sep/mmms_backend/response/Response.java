package com.sep.mmms_backend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Response {
    String message;
    Object mainBody;

    public Response(String message) {
        this.message = message;
    }

    public Response(ResponseMessage responseMessage) {
        this.message = responseMessage.getMessage();
    }
}
