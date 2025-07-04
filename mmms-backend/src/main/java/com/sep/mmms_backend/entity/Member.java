package com.sep.mmms_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private int memberId;

    @Column(name="first_name", nullable=false)
    @NotBlank
    private String firstName;

    @Column(name="last_name", nullable=false)
    @NotBlank
    private String lastName;

    private String institution;  //example: Pulchowk Campus, IOE
    private String post; //example: professor
    private String qualitifcation; //example: Dr

    @Column
    @Email
    private String email;

    @Column(name = "created_by", updatable = false, nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date", updatable = false, nullable = false)
    @CreatedDate
    private LocalDate createdDate;


    @Column(name = "modified_by",  nullable = false)
    @CreatedBy
    private String modifiedBy;

    @Column(name = "modified_date", nullable = false)
    @CreatedDate
    private LocalDate modifiedDate;

    @OneToMany(mappedBy="member")
    private Set<CommitteeMembership> memberships;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="meeting_attendees",
            joinColumns = {
                    @JoinColumn(name="member_id", referencedColumnName = "member_id"),
            },

            inverseJoinColumns = {
                    @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id"),
            }
    )
    List<Meeting> attendedMeetings;
}
