package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.exceptions.UserDoesNotExist;
import com.sep.mmms_backend.exceptions.UsernameAlreadyExistsException;
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


            return ResponseEntity.badRequest().body(new Response(ResponseMessages.USER_REGISTER_FAILED, errorMessages));
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



    @PostMapping("/api/updateUser")
    public ResponseEntity<Response> updateUser(@RequestBody @Valid AppUser appUser, Errors errors, Authentication authentication) {
        if(appUser.getUid() <=0) {
            log.error("Improper request format: {}", ResponseMessages.ROUTE_UPDATE_USER_MISUSED);
            return ResponseEntity.badRequest().body(new Response(ResponseMessages.ROUTE_UPDATE_USER_MISUSED));
        }

        //make sure that the use whose data to be updated is the current user
        //use uid to do that instead of username as the username can also be changed
        if(!(appUserService.loadUserByUsername(authentication.getName()).getUid() == appUser.getUid())) {
            log.error(ResponseMessages.USER_CAN_ONLY_UPDATE_THEIR_OWN_DATA.toString());
            return ResponseEntity.badRequest().body(new Response(ResponseMessages.USER_CAN_ONLY_UPDATE_THEIR_OWN_DATA));
        }

        if(errors.hasErrors()) {
            HashMap<String, ArrayList<String>> errorMessages = new HashMap<>();
            errors.getFieldErrors().forEach(
                    error-> {
                        if(!errorMessages.containsKey(error.getField())){
                            ArrayList<String> list = new ArrayList<>();
                            list.add(error.getDefaultMessage());
                            errorMessages.put(error.getField(),list);
                        } else {
                            errorMessages.get(error.getField()).add(error.getDefaultMessage());
                        }
                   }
            );

            log.error("Validation Failed while updating user: {}", errorMessages);
            return ResponseEntity.badRequest().body(new Response(ResponseMessages.USER_UPDATION_FAILED));
        }

        try {
            appUserService.updateUser(appUser);
        } catch(UserDoesNotExist e) {
            log.error("User with the username `{}` does not exist", appUser.getUsername());
            return ResponseEntity.badRequest().body(new Response(e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response(ResponseMessages.USER_UPDATION_SUCCESS));
    }
}
