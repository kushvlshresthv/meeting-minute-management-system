package com.sep.mmms_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
    private Integer id;

    /**
     * A universally unique identifier (UUID) that serves as the business key.
     * This is the field used for equals() and hashCode().
     */
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

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
    private Set<Meeting> meetings;

    @OneToMany(mappedBy = "committee", cascade = CascadeType.PERSIST)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<CommitteeMembership> memberships = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Committee that = (Committee) o;
        return Objects.equals(uuid, that.uuid);
    }
}
