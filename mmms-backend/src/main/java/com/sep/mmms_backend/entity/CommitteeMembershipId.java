package com.sep.mmms_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor

public class CommitteeMembershipId {
    @Column(name="committee_id")
    private Long committeeId;

    @Column(name="member_id")
    private Long memberId;
}
