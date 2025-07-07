package com.sep.mmms_backend.controller;

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
            "email": "dahalrajesh@gmail.com",
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
}

