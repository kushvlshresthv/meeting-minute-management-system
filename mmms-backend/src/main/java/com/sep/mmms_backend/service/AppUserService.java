package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.UserDoesNotExist;
import com.sep.mmms_backend.exceptions.UsernameAlreadyExistsException;
import com.sep.mmms_backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AppUser saveNewUser(AppUser appUser) {

        if(appUserRepository.existsByUsername(appUser.getUsername())) {
            throw new UsernameAlreadyExistsException(ExceptionMessages.USERNAME_ALREADY_EXISTS.toString());
        }

        return appUserRepository.save(appUser);
    }

    public AppUser updateUser(AppUser appUser) {
        if(!appUserRepository.existsById(appUser.getUid())) {
            throw new UserDoesNotExist(ExceptionMessages.USER_DOES_NOT_EXIST);
        }
        return appUserRepository.save(appUser);
    }


}
