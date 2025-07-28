package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class MemberWithoutCommitteeDto {

    private final int memberId;
    private final String firstName;
    private final String lastName;
    private final String institution;
    private final String post;
    private final String qualification;

    public MemberWithoutCommitteeDto(Member member){
        this.memberId = member.getId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.institution = member.getInstitution();
        this.post = member.getPost();
        this.qualification = member.getQualification();
    }
}
