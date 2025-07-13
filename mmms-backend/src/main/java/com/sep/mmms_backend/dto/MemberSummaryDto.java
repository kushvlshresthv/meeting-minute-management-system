package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberSummaryDto {
    private final int memberId;
    private final String firstName;
    private final String lastName;
    private final String institution;
    private final String post;
    private CommitteeMembershipDto membership;

    public MemberSummaryDto(Member member, int committeeId) {
        this.memberId = member.getId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.institution = member.getInstitution();
        this.post = member.getPost();
        for(CommitteeMembership membership: member.getMemberships() ) {
            if(membership.getId().getCommitteeId() == committeeId) {
                this.membership = new CommitteeMembershipDto(membership);
                break;
            }
        }
    }
}
