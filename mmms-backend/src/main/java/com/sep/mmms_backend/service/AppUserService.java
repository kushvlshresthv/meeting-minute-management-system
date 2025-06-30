package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.Optional;

@Slf4j
@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    @Autowired
    private Validator validator;

    AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser loadUserByUsername(String username) {
        if(username == null) {
            throw new UserDoesNotExistException("Username is null");
        }
        Optional<AppUser> user =  appUserRepository.findByUsername(username);

        if(user.isPresent()) {
            return user.get();
        } else {
            throw new UserDoesNotExistException(ExceptionMessages.USER_DOES_NOT_EXIST);
        }
    }

    public AppUser saveNewUser(AppUser appUser) {

        if(appUserRepository.existsByUsername(appUser.getUsername())) {
            throw new UsernameAlreadyExistsException(ExceptionMessages.USERNAME_ALREADY_EXISTS.toString());
        }

        return appUserRepository.save(appUser);
    }


    /**
     * This method is used to update AppUser's firstName, lastName, email, username
     * @param updatedUserData: data to be updated
     * @param currentUsername: the username invoking this method
     * @return saved AppUser Object
     */
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

        //if the fields are null or blank, the keep the existing data
        if(updatedUserData.getFirstName()!=null && updatedUserData.getFirstName().isBlank()) {
            currentUser.setFirstName(updatedUserData.getFirstName());
        }
        if(updatedUserData.getLastName()!=null && updatedUserData.getLastName().isBlank()) {
            currentUser.setLastName(updatedUserData.getLastName());
        }
        if(updatedUserData.getEmail()!=null && updatedUserData.getEmail().isBlank()) {
            currentUser.setEmail(updatedUserData.getEmail());
        }

        //workaround so that the validation does not fail
        currentUser.setConfirmPassword(currentUser.getPassword());

        //performing validations for the AppUser object with the new updated data
        BindingResult bindingResult = new BeanPropertyBindingResult(currentUser, "user");
        validator.validate(currentUser, bindingResult);



        if(bindingResult.hasErrors()) {
            throw new ValidationFailureException(ExceptionMessages.USER_VALIDATION_FALIED, bindingResult);
        }

        return appUserRepository.save(currentUser);
    }
}
