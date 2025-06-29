package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.AppUserRepository;
import com.sep.mmms_backend.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser loadUserByUsername(String username) {
        if(username == null) {
            throw new UserDoesNotExist("Username is null");
        }
        Optional<AppUser> user =  appUserRepository.findByUsername(username);

        if(user.isPresent()) {
            return user.get();
        } else {
            throw new UserDoesNotExist(ExceptionMessages.USER_DOES_NOT_EXIST);
        }
    }

    public AppUser saveNewUser(AppUser appUser) {

        if(appUserRepository.existsByUsername(appUser.getUsername())) {
            throw new UsernameAlreadyExistsException(ExceptionMessages.USERNAME_ALREADY_EXISTS.toString());
        }

        return appUserRepository.save(appUser);
    }

    public AppUser updateUser(AppUser updatedUserData, String currentUsername) {
        AppUser currentUser = loadUserByUsername(currentUsername);

        //make sure that the user whose data to be updated is the current user
        if(currentUser.getUid() != updatedUserData.getUid()) {
            throw new UnauthorizedUpdateException(ExceptionMessages.USER_CAN_ONLY_UPDATE_THEIR_OWN_DATA);
        }


        //check whether the password is being changed or not

        if (updatedUserData.getPassword() != null && !currentUser.getPassword().equals(updatedUserData.getPassword())) {
            throw new PasswordChangeNotAllowedException(ExceptionMessages.ROUTE_UPDATE_USER_CANT_UPDATE_PASSWORD);
        }

        log.info("TODO: Before updating the user, also check whether the username is available or not");

        if(updatedUserData.getFirstName()!=null) {
            currentUser.setFirstName(updatedUserData.getFirstName());
        }
        if(updatedUserData.getLastName()!=null) {
            currentUser.setLastName(updatedUserData.getLastName());
        }
        if(updatedUserData.getEmail()!=null) {
            currentUser.setEmail(updatedUserData.getEmail());
        }
        return appUserRepository.save(currentUser);
    }
}
