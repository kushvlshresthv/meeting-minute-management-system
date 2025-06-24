package com.sep.mmms_backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.mmms_backend.global_constants.ValidationErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Entity(name="app_meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//this registers the 'AuditingEntityListener' with the entity which automatically updates the auditing fields(like createdDate, lastModifiedDate etc), when lifecycle events such as creation or updation occurs
@EntityListeners(AuditingEntityListener.class)
//TODO: add 'Address' to the Meeting
//TODO: add 'Attendes' and 'Absentes' field for Meeting

public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="meeting_id")
    private int meetingId;

    @NotBlank(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_name")
    private String meetingName;

    @Column(name="metting_description")
    private String meetingDescription;

    @NotNull(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_held_date")
    private LocalDate meetingHeldDate;

    @JsonIgnore
    @CreatedBy
    @Column(name="created_by", updatable = false)
    private String createdBy;

    @JsonIgnore
    @LastModifiedBy
    @Column(name="updated_by")
    private String updatedBy;

    @JsonIgnore
    @CreatedDate
    @Column(name="created_date", updatable=false, nullable = false)
    private LocalDate createdDate;

    @JsonIgnore
    @LastModifiedDate
    @Column(name="updated_date", nullable = false)
    private LocalDate updatedDate;

    @ManyToMany(mappedBy="attendedMeetings", fetch = FetchType.LAZY)
    public List<AppUser> attendees;

    @ManyToMany(mappedBy="unattendedMeetings", fetch = FetchType.LAZY)
    public List<AppUser> absentees;
}
