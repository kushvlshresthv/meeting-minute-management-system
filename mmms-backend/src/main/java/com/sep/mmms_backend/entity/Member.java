package com.sep.mmms_backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity(name="members")
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private int id;

    @Column(name="first_name", nullable=false)
    @NotBlank
    private String firstName;

    @Column(name="last_name", nullable=false)
    @NotBlank
    private String lastName;

    private String institution;  //example: Pulchowk Campus, IOE
    private String post; //example: professor
    private String qualification; //example: Dr

    @Column
    @Email
    private String email;

    @Column(name = "created_by", updatable = false, nullable = false)
    @CreatedBy
    @JsonIgnore
    private String createdBy;

    @Column(name = "created_date", updatable = false, nullable = false)
    @CreatedDate
    private LocalDate createdDate;

    @JsonIgnore
    @Column(name = "modified_by",  nullable = false)
    @CreatedBy
    private String modifiedBy;

    @JsonIgnore
    @Column(name = "modified_date", nullable = false)
    @CreatedDate
    private LocalDate modifiedDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy="member", cascade=CascadeType.PERSIST)
    private List<CommitteeMembership> memberships = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
    List<Meeting> attendedMeetings = new ArrayList<>();
}
