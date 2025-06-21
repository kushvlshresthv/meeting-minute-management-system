package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.exceptions.UsernameAlreadyExistsException;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

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
           HashMap<String, ArrayList<String>> errorMessages = new HashMap<>();

//           errors.getFieldErrors()
//                    .forEach(error-> errorMessages.put(error.getField(), error.getDefaultMessage()));

            errors.getFieldErrors().forEach(
                   error-> {
                       //if the key is not already present in the Map, create the key as well as new ArrayList for the value
                       if(!errorMessages.containsKey(error.getField())){
                           ArrayList<String> list = new ArrayList<>();
                           list.add(error.getDefaultMessage());
                           errorMessages.put(error.getField(),list);
                       } else {
                           errorMessages.get(error.getField()).add(error.getDefaultMessage());
                       }
                   }
            );
            log.error("Validation Failed while registering user: {}", errorMessages);


            return ResponseEntity.badRequest().body(new Response(ResponseMessages.REGISTER_USER_FAILED, errorMessages));
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
        if(savedUser != null && savedUser.getUid() <=0) {
            return ResponseEntity.internalServerError().body(null);
        }

        log.info("User with the username @{} registered successfully", appUser.getUsername());
        return ResponseEntity.ok().body(new Response(ResponseMessages.REGISTER_USER_SUCCESS));
    }
}
