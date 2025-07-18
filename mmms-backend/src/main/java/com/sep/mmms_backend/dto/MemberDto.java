package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class MemberDto {

    private final int memberId;
    private final String firstName;
    private final String lastName;
    private final String institution;
    private final String post;
    private final String qualification;
    private final LocalDate createdDate;
    private CommitteeMembershipDto membership;


    /**
     *
     * This creates a MemberDto for a member BELONGING to a particular committee
     *
     * A member can belong to several committees, but this DTO only handles the member of a particular committee
     *
     */

    public MemberDto(Member member, int committeeId) {
        this.memberId = member.getId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.institution = member.getInstitution();
        this.post = member.getPost();
        this.qualification = member.getQualification();
        this.createdDate = member.getCreatedDate();
        for(CommitteeMembership membership: member.getMemberships() ) {
            if(membership.getId().getCommitteeId() == committeeId) {
                this.membership = new CommitteeMembershipDto(membership);
                break;
            }
        }
    }
}
