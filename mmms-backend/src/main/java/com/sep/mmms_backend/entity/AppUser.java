package com.sep.mmms_backend.entity;

import com.sep.mmms_backend.validators.annotations.CheckUsernameAvailability;
import com.sep.mmms_backend.validators.annotations.FieldsValueMatch;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name="app_users")
@AllArgsConstructor
@Builder
@NoArgsConstructor

@FieldsValueMatch.List({
        @FieldsValueMatch(field = "password", fieldMatch = "confirmPassword", message = "Passwords must match"),
})
public class AppUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int uid;

    @NotEmpty
    @Column(name="firstname")
    String firstName;

    @NotEmpty
    @Column(name="lastname")
    String lastName;

    @Column(name="username")
    @NotEmpty
    @CheckUsernameAvailability
    String username;

    @NotEmpty
    @Email
    @Column(name="email")
    String email;

    @NotEmpty
    @Column(name="password")
    String password;

    @NotEmpty
    @Transient
    String confirmPassword;

    //TODO: add the joined meetings as well as the 'post' of the person in the institution
}

/*
{
    "uid": 1,
    "firstName": "John",
    "lastName": "Doe",
    "username": "John",
    "email": "JohnDoe@gmail.com",
    "password":"johndoe",
    "confirmPassword":"johndoe"
}
 */