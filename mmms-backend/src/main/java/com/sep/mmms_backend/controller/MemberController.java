package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.MemberDetailsDto;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/searchMembersByName")
    public ResponseEntity<Response> getMembersByName(@RequestParam(required=true) String name) {
        List<Member> fetchedMembers = memberService.searchMemberByName(name);
        return ResponseEntity.ok(new Response(fetchedMembers));
    }


    /**
     *
     * @param committeeId id of the committee to which the new memeber is going to be associated with
     * @param member member data to be persisted
     */

    /*
        a valid request json example
        {
            "firstName": "Rajesh",
            "lastName": "Dahal",
            "institution": "Tribhuvan University",
            "post": "Professor",
            "qualification": "Er",
            "email": "daha@gmail.cou",
            "memberships: [
                {
                    "role": "SECRETARY",
                }
            ]
        }
     */

    @PostMapping("/createMember")
    public ResponseEntity<Response> createMember(@RequestParam(required = true) int committeeId, @RequestBody(required=true) Member member, Authentication authentication) {
        memberService.saveNewMember(member, committeeId, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_CREATION_SUCCESS));
    }

    /** IMPORTANT NOTE:

     * whether a particular member is acceesbile by a particular user is checked by comparing username

     * if the username is made changeable in the future, this has to be updated as well

     * the resason the 'created_by' section was not choosen to be 'id' is because, to retreive the 'id' of the current user, a database operation is required, which flushes the context to save the entity before the 'created_by' section is populated in the entity causing an error
     */
    @GetMapping("/getMemberDetails")
    public ResponseEntity<Response> getMemberDetails(@RequestParam(required=true) int memberId, Authentication authentication) {
        MemberDetailsDto memberDetailsDto = memberService.getMemberDetails(memberId, authentication.getName());

        return ResponseEntity.ok(new Response(memberDetailsDto));
    }
}

