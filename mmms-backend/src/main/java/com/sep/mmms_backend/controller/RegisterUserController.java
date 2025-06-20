package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessage;
import com.sep.mmms_backend.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class RegisterUserController {
    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    RegisterUserController(AppUserService appUserService, PasswordEncoder passwordEncoder ) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid AppUser appUser, Errors errors) {
        if(errors.hasErrors()){
            List<String> errorMessages = errors.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            log.error("Validation Failed while registering user: {}", errorMessages);
            return ResponseEntity.ok().body(new Response(ResponseMessage.REGISTER_USER_FAILED, errorMessages));
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUserService.saveUser(appUser);
        log.info("User with the username @{} registered successfully", appUser.getUsername());
        return ResponseEntity.ok().body(new Response(ResponseMessage.REGISTER_USER_SUCCESS));
    }
}
