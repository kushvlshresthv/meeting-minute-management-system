package com.sep.mmms_backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

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
    private int id;

    @NotBlank(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_title")
    private String title;

    @Column(name="meeting_description")
    private String description;

    @NotNull(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_held_date")
    private LocalDate heldDate;

    @NotNull(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_held_time")
    private LocalTime heldTime;

    @NotNull(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    @Column(name="meeting_held_place")
    private String heldPlace;

    @JsonIgnore
    @CreatedBy
    @Column(name="created_by", updatable = false)
    private String createdBy;

    @JsonIgnore
    @LastModifiedBy
    @Column(name="updated_by")
    private String updatedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name="meeting_attendees",
            inverseJoinColumns = {
                    @JoinColumn(name="member_id", referencedColumnName = "member_id"),
            },

            joinColumns = {
                    @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id"),
            }
    )
    public Set<Member> attendees;

    @OneToMany(mappedBy="meeting", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Decision> decisions;

    @OneToOne
    @JoinColumn(name="coordinator", referencedColumnName = "member_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "meeting coordinator should be specified")
    private Member coordinator;

    public boolean equals(Meeting meeting) {
        if(this == meeting) return true;
        if(meeting == null) return false;
        return id == meeting.getId();
    }
}
