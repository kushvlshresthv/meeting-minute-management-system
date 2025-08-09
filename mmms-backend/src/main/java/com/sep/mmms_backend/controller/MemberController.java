package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.MemberCreationDto;
import com.sep.mmms_backend.dto.MemberDetailsDto;
import com.sep.mmms_backend.dto.MemberSummaryDto;
import com.sep.mmms_backend.dto.MemberWithoutCommitteeDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final CommitteeService committeeService;

    public MemberController(MemberService memberService, CommitteeService committeeService) {
        this.memberService = memberService;
        this.committeeService = committeeService;
    }

    @GetMapping("/searchMembersByName")
    public ResponseEntity<Response> getMembersByName(@RequestParam(required=true) String name) {
        List<Member> fetchedMembers = memberService.searchMemberByName(name);
        return ResponseEntity.ok(new Response(fetchedMembers));
    }


    /**
     *
     * @param committeeId id of the committee to which the new memeber is going to be associated with
     * @param memberDto member data to be persisted
     */


    @PostMapping("/createMember")
    public ResponseEntity<Response> createMember(@RequestParam(required = true) int committeeId, @RequestBody(required=true) MemberCreationDto memberDto , Authentication authentication) {
       Committee committee= committeeService.findCommitteeById(committeeId);
       Member member = memberService.saveNewMember(memberDto, committee, authentication.getName());

       MemberSummaryDto memberSummaryDto = new MemberSummaryDto(member, committeeId);

        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_CREATION_SUCCESS, memberSummaryDto));
    }

    /** <h2>IMPORTANT NOTE</h2>
     * whether a particular member is acceesbile by a particular user is checked by comparing username
       <br> <br>
     * if the username is made changeable in the future, this has to be updated as well
       <br> <br>
     * the resason the 'created_by' section was not choosen to be 'id' is because, to retreive the 'id' of the current user, a database operation is required, which flushes the context to save the entity before the 'created_by' section is populated in the entity causing an error
     */

    @GetMapping("/getMemberDetails")
    public ResponseEntity<Response> getMemberDetails(@RequestParam(required=true) int memberId, Authentication authentication) {
        MemberDetailsDto memberDetailsDto = memberService.getMemberDetails(memberId, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_DETAIL_RETRIEVED_SUCCESSFULLY, memberDetailsDto));
    }


    @GetMapping("/getAllMembers")
    public ResponseEntity<Response> getAllMembers(Authentication    authentication) {
       List<MemberWithoutCommitteeDto> allMembers =  memberService.getAllMembers(authentication.getName());
       return ResponseEntity.ok(new Response(allMembers));
    }
}

