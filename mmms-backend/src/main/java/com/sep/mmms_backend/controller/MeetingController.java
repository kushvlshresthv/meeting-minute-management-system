package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.MeetingCreationDto;
import com.sep.mmms_backend.dto.MeetingDetailsDto;
import com.sep.mmms_backend.dto.MeetingSummaryDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

@RestController
@RequestMapping("api")
public class MeetingController {
   private MeetingService meetingService;
   private CommitteeService committeeService;

   MeetingController(MeetingService meetingService, CommitteeService committeeService) {
        this.meetingService = meetingService;
        this.committeeService = committeeService;
   }


    /*
    {
      "meetingHeldDate": "2025-07-09",
      "meetingHeldPlace": "Kathmandu, Nepal",
      "meetingHeldTime": "14:30:00",
      "attendees": [
        {
          "memberId": 101
        },
        {
          "memberId": 102
        },
        {
          "memberId": 103
        }
      ],
      "decisions": [
        {
          "decision": "Approved annual budget."
        },
        {
          "decision": "Scheduled next meeting for August."
        }
      ]
    }
    */

    //TODO: Create Tests
    @PostMapping("/createMeeting")
    public ResponseEntity<Response> createMeeting(
            @RequestBody(required = true) MeetingCreationDto meetingCreationDto,
            @RequestParam(required=true) int committeeId,
            Authentication authentication) {

        Committee committee = committeeService.findCommitteeById(committeeId);

        Meeting savedMeeting =  meetingService.saveNewMeeting(meetingCreationDto, committee, authentication.getName());

        MeetingSummaryDto savedMeetingSummary = new MeetingSummaryDto(savedMeeting);
        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_CREATION_SUCCESSFUL, savedMeetingSummary));
    }


    //TODO: Create Tests
    @PostMapping("addAttendeesToMeeting")
    public ResponseEntity<Response> addAttendeesToMeeting(@RequestParam int committeeId, @RequestParam int meetingId, @RequestBody LinkedHashSet<Integer> newAttendeeIds, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Meeting meeting = meetingService.findMeetingById(meetingId);

        meetingService.addAttendeesToMeeting(newAttendeeIds, committee, meeting, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_ATTENDEE_ADDITION_SUCCESS));
    }

    //TODO: Create Tests
    @GetMapping("getMeetingDetails")
    public ResponseEntity<Response> getMeetingDetails(@RequestParam int committeeId, @RequestParam int meetingId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Meeting meeting = meetingService.findMeetingById(meetingId);
        Meeting meetingDetails = meetingService.getMeetingDetails(committee, meeting, authentication.getName());

        MeetingDetailsDto meetingDto = new MeetingDetailsDto(meetingDetails);
        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_ATTENDEE_ADDITION_SUCCESS, meetingDto));
    }


    @PostMapping("/updateMeeting")
    public ResponseEntity<Response> updateMeeting(@RequestBody @Valid Meeting meeting, Errors errors) {

        //Validation failed:
        if(errors.hasErrors()) {
            HashMap<String, ArrayList<String>> errorMessages = new HashMap<>();
            errors.getFieldErrors().forEach(
                    config-> {
                        if(errorMessages.containsKey(config.getField())) {
                            errorMessages.get(config.getField()).add(config.getDefaultMessage());
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add(config.getDefaultMessage());
                            errorMessages.put(config.getField(), list);
                        }
                    }
            );

            return ResponseEntity.badRequest().body(new Response(ResponseMessages.MEETING_CREATION_FAILED, errorMessages));
        }

        try {
            Meeting savedMeeting = this.meetingService.updateMeeting(meeting);
        } catch(MeetingDoesNotExistException e) {
            return ResponseEntity.badRequest().body(new Response(e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(ResponseMessages.MEETING_CREATION_SUCCESSFUL));
    }
}
