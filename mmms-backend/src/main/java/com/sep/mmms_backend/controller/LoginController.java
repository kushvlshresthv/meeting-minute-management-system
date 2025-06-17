package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.global_constants.GlobalConstants;
import com.sep.mmms_backend.response.Response;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = GlobalConstants.FRONTEND_URL, allowCredentials="true", exposedHeaders="*", allowedHeaders = "*")
@RestController
public class LoginController {
    // "/login" is a secure rest end point handled by Spring Security
    @GetMapping("/login")
    public ResponseEntity<Response> tryLogin(HttpSession session) {
        return new ResponseEntity<Response>(new Response("login successful"), HttpStatus.OK);
    }
}
