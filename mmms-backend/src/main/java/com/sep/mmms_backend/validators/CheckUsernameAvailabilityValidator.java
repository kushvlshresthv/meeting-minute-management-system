package com.sep.mmms_backend.validators;


import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.service.AppUserService;
import com.sep.mmms_backend.validators.annotations.CheckUsernameAvailability;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CheckUsernameAvailabilityValidator implements ConstraintValidator<CheckUsernameAvailability, String> {
    @Autowired
    AppUserService appUserService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]");

        Matcher matcher = pattern.matcher(username);

        if (matcher.find()) {
            return false;
        } else {
            AppUser user = appUserService.loadUserByUsername(username);
            if (user == null) {
                return true;
            }
            return false;
        }
    }


    @Override
    public void initialize(CheckUsernameAvailability constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

}
