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

@Entity(name="meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="meeting_id")
    private int meetingId;

    @NotBlank(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_name")
    private String meetingName;

    @Column(name="meeting_description")
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

    @ManyToOne
    @JoinColumn(name = "committee_id", referencedColumnName="committee_id")
    @JsonIgnore
    Committee committee;

    @ManyToMany(mappedBy="attendedMeetings", fetch = FetchType.LAZY)
    public List<Member> attendees;

    @OneToMany(mappedBy="meeting")
    @JsonIgnore
    private List<Decision> decisions;
}
