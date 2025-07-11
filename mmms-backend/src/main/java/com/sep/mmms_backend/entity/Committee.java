package com.sep.mmms_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="committees")
@EntityListeners(AuditingEntityListener.class)
public class Committee {
    @Id
    @Column(name="committee_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="committee_name", nullable = false)
    @NotBlank
    private String name;

    @Column(name="committee_description")
    private String description;

    @ManyToOne
    @JoinColumn(name="created_by", referencedColumnName="uid", nullable=false)
    @JsonIgnore
    private AppUser createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDate createdDate;

    @Column(name = "modified_by")
    @LastModifiedBy
    @JsonIgnore
    private String modifiedBy;

    @Column(name = "modified_date")
    @LastModifiedDate
    @JsonIgnore
    private LocalDate modifiedDate;

    @OneToMany(mappedBy="committee")
    private List<Meeting> meetings;

    @OneToMany(mappedBy = "committee", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<CommitteeMembership> memberships = new ArrayList<>();
}
