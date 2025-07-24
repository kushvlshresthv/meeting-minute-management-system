package com.sep.mmms_backend.dto;


/**
 * This DTO is being used in /addMembersToCommitteeDto
 */
public record NewMembershipRequest(Integer memberId, String role) {

}
