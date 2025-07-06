package com.sep.mmms_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private int committeeId;

    @Column(name="committee_name", nullable = false)
    @NotBlank
    private String committeeName;

    @Column(name="committee_description")
    private String committeeDescription;

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

    @OneToMany(mappedBy = "committee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CommitteeMembership> memberships;
}
