package com.sep.mmms_backend.entity;

import com.sep.mmms_backend.global_constants.ValidationErrorMessages;
import com.sep.mmms_backend.validators.annotations.FieldsValueMatch;
import com.sep.mmms_backend.validators.annotations.UsernameFormat;
import jakarta.persistence.*;
import jakarta.validation.Validation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.sep.mmms_backend.global_constants.ValidationErrorMessages.*;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name="app_users")
@AllArgsConstructor
@Builder
@NoArgsConstructor

@FieldsValueMatch.List({
        @FieldsValueMatch(field = "password", fieldMatch = "confirmPassword", message = PASSWORD_CONFIRMPASSWORD_MISMATCH),
})
public class AppUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int uid;


    @NotBlank(message = FIELD_CANNOT_BE_EMPTY)
    @Column(name="firstname")
    String firstName;

    @NotBlank(message = FIELD_CANNOT_BE_EMPTY)
    @Column(name="lastname")
    String lastName;

    @Column(name="username")
    @NotBlank(message = FIELD_CANNOT_BE_EMPTY)
    @UsernameFormat
    String username;

    @NotBlank(message = FIELD_CANNOT_BE_EMPTY)
    @Email(message= VALID_EMAIL_REQUIRED)
    @Column(name="email")
    String email;

    @NotBlank(message = FIELD_CANNOT_BE_EMPTY)
    @Column(name="password")
    @Size(min=5, message= CHOOSE_STRONGER_PASSWORD)
    String password;

    @NotEmpty(message = FIELD_CANNOT_BE_EMPTY)
    @Transient
    String confirmPassword;

    //TODO: add the joined meetings as well as the 'post' of the person in the institution

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="user_attended_meetings",
        joinColumns = {
            @JoinColumn(name="user_id", referencedColumnName = "uid"),
        },

        inverseJoinColumns = {
            @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id"),
        }
    )
    List<Meeting> attendedMeetings;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="user_unattended_meeting",
            joinColumns = {
                    @JoinColumn(name="user_id", referencedColumnName = "uid"),
            },

            inverseJoinColumns = {
                    @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id"),
            }
    )
    List<Meeting> unattendedMeetings;
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