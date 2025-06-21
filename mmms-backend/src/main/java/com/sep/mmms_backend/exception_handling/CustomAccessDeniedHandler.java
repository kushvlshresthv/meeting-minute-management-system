package com.sep.mmms_backend.exception_handling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
//handles instances when a route that does not exists is invoked
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
       response.setContentType("application/json;charset=utf-8");
       response.setStatus(HttpServletResponse.SC_FORBIDDEN);

       Response errorResponse = new Response();
       errorResponse.setMessage(ResponseMessages.ACCESS_DENIED +": " + accessDeniedException.getMessage());

       ObjectMapper mapper = new ObjectMapper();
       mapper.writeValue(response.getWriter(), errorResponse);

        log.error("{}: {} ", ResponseMessages.ACCESS_DENIED , accessDeniedException.getMessage());
    }
}
