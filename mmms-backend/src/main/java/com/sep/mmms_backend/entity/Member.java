package com.sep.mmms_backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="members")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Integer id;

    @Column(name="first_name", nullable=false)
    @NotBlank(message="member's first name can't be blank")
    private String firstName;

    @Column(name="last_name", nullable=false)
    @NotBlank(message="member's last name can't be blank")
    private String lastName;

    private String institution;  //example: Pulchowk Campus, IOE

    @NotBlank(message="member's post can't be blank")
    private String post; //example: professor

    @NotBlank(message="member's qualification can't be blank")
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
    @NotEmpty
    private Set<CommitteeMembership> memberships = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
    Set<Meeting> attendedMeetings = new HashSet<>();
}
