package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private int memberId;
    private String firstName;
    private String lastName;
    private String institution;
    private String post;
    private String qualification;
    private LocalDate createdDate;

    public MemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.institution = member.getInstitution();
        this.post = member.getPost();
        this.qualification = member.getQualification();
        this.createdDate = member.getCreatedDate();
    }
}
