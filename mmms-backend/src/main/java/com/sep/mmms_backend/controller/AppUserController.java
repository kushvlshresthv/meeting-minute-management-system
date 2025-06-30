package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.UsernameAlreadyExistsException;
import com.sep.mmms_backend.exceptions.ValidationFailureException;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@Slf4j
public class AppUserController {
    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    AppUserController(AppUserService appUserService, PasswordEncoder passwordEncoder ) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid AppUser appUser, Errors errors) {
        if(appUser.getUid() > 0) {
            log.error(ResponseMessages.ROUTE_REGISTER_MISUED.toString());
            return ResponseEntity.badRequest().body(new Response(ResponseMessages.ROUTE_REGISTER_MISUED));
        }

        if(errors.hasErrors()){
            throw new ValidationFailureException(ExceptionMessages.USER_VALIDATION_FALIED, errors);
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        //save the data to the database
        AppUser savedUser = null;

        //validate that the username is unique
        try {
            savedUser = appUserService.saveNewUser(appUser);
        } catch(UsernameAlreadyExistsException e) {
            log.error("User with the username `{}` already exists", appUser.getUsername());
            return ResponseEntity.badRequest().body(new Response(e.getMessage()));
        }

        log.info("User with the username @{} registered successfully", appUser.getUsername());
        return ResponseEntity.ok().body(new Response(ResponseMessages.USER_REGISTER_SUCCESS));
    }




    //this route does not allow the users to change the password
    //validation for the incoming requestion body is performed inside the Service class
    @PostMapping("/api/updateUser")
    public ResponseEntity<Response> updateUser(@RequestBody AppUser appUser, Authentication authentication) {

        //check if the uid is present in the request body
        if(appUser.getUid() <=0) {
            log.error("Improper request format: {}", ResponseMessages.ROUTE_UPDATE_USER_MISUSED);
            return ResponseEntity.badRequest().body(new Response(ResponseMessages.ROUTE_UPDATE_USER_MISUSED));
        }

        //save the user
        appUserService.updateUser(appUser,authentication.getName());

        return ResponseEntity.ok().body(new Response(ResponseMessages.USER_UPDATION_SUCCESS));
    }
}
