package com.sep.mmms_backend.controller;


import com.sep.mmms_backend.dto.CommitteeCreationDto;
import com.sep.mmms_backend.dto.CommitteeDetailsDto;
import com.sep.mmms_backend.dto.CommitteeSummaryDto;
import com.sep.mmms_backend.dto.NewMembershipRequest;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
    }


    @PostMapping("/createCommittee")
    public ResponseEntity<Response> createCommittee(@RequestBody CommitteeCreationDto committeeCreationDto, Authentication authentication) {
        Committee savedCommittee = committeeService.saveNewCommittee(committeeCreationDto, authentication.getName());
        CommitteeSummaryDto committeeSummaryDto = new CommitteeSummaryDto(savedCommittee);
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEE_CREATION_SUCCESS, committeeSummaryDto));
    }

    @GetMapping("/getCommittee")
    public ResponseEntity<Response> getCommittees(Authentication authentication) {
        List<Committee> committees =  committeeService.getCommittees(authentication.getName());
        return ResponseEntity.ok().body(new Response(committees));
    }




    /**
     * this route fetches the committee from the database, checks if the committee is accessible by the current user
     *
     * then fetches all the members associated with the committee and sends both of them as response
     */


    @GetMapping("/getCommitteeDetails")
    public ResponseEntity<Response> getCommitteeDetails(@RequestParam int committeeId, Authentication authentication) {
        CommitteeDetailsDto committeeDetails = committeeService.getCommitteeDetails(committeeId, authentication.getName());
        return ResponseEntity.ok().body(new Response(committeeDetails));
    }

    @PostMapping("/addMembersToCommittee")
    public ResponseEntity<Response> addMembershipsToCommittee(@RequestParam int committeeId, @RequestBody Set<NewMembershipRequest> newMemberships, Authentication authentication) {
        List<Member> newlyAddedMembers = committeeService.addMembershipsToCommittee(committeeId, newMemberships, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_MEMBER_ADDITION_SUCCESS, newlyAddedMembers));
    }
}
