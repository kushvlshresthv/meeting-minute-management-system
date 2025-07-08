package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeDetailsDto {
    private Committee committee;
    private List<MemberDto> members;
}
