package com.sep.mmms_backend.validators.annotations;

import com.sep.mmms_backend.validators.FieldsValueMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target({ElementType.TYPE})   //used on top of a class
@Retention(RetentionPolicy.RUNTIME)

public @interface FieldsValueMatch {
    Class<?> [] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String message() default "Fields value don't match";

    String field();
    String fieldMatch();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsValueMatch[] value();
    }
}

