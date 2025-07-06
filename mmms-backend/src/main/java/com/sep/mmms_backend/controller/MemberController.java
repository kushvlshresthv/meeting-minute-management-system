package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

