package com.sep.mmms_backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.mmms_backend.entity.CommitteeMembership;
import lombok.Getter;

@Getter
public class CommitteeMembershipDto {
    private final String role;

    public CommitteeMembershipDto(CommitteeMembership committeeMembership) {
        this.role = committeeMembership.getRole();
    }


    @JsonCreator
    public CommitteeMembershipDto (@JsonProperty("role") String role){
        this.role = role;
    }
}
