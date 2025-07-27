package com.sep.mmms_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.Objects;
import java.util.UUID;

/**
 * NOTE: CommitteeMembership is uniquely identified by a combination of committee_id and member_id, hence this entity will have a composite primary key.
 */

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="committee_memberships")
public class CommitteeMembership implements Persistable<CommitteeMembershipId> {
    /**
     * EmbeddedId has been used in order to have a composite primary key for this entity
     */
    @EmbeddedId
    private CommitteeMembershipId id = new CommitteeMembershipId();

    /**
     * when committee field of this entity is populated, the @EmbeddedId is also populated due to @MapsId used
     */

    @ManyToOne(fetch= FetchType.LAZY)
    @MapsId("committeeId")  //it maps a relationship field to a part of the embedded primary key
    @JoinColumn(name="committee_id", referencedColumnName="committee_id")
    private Committee committee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name="member_id", referencedColumnName="member_id")
    private Member member;


    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(name="role", nullable=false)
    @NotBlank(message = "Role must be defined when adding the users to a committee")
    private String role;

    @Transient
    private boolean isNew = true;

    @Override
    public CommitteeMembershipId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostLoad
    void markNotNew() {
        this.isNew = false;
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    @PrePersist
    public void initUUID() {
        markNotNew();
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

        CommitteeMembership that = (CommitteeMembership) o;
        return Objects.equals(uuid, that.uuid);
    }
}
