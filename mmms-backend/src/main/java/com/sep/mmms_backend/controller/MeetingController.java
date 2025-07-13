package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.MeetingAlreadyExistsException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api")
public class MeetingController {
   MeetingService meetingService;

   MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
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

    @PostMapping("committee/createMeeting")
    public ResponseEntity<Response> createMeeting(@RequestBody(required = true) Meeting meeting, @RequestParam(required=true) int committeeId, Authentication authentication) {
       Meeting savedMeeting =  meetingService.saveNewMeeting(meeting, committeeId, authentication.getName());
       return ResponseEntity.ok(new Response(savedMeeting));
    }


    /**
     * The received attendees are not well populated, only the memberId is populated
     */
    @PostMapping("addAttendeesToMeeting")
    public ResponseEntity<Response> addAttendeesToMeeting(@RequestParam int committeeId, @RequestParam int meetingId, @RequestBody Set<Integer> newAttendeeIds, Authentication authentication) {
        Set<Member> newAttendees = meetingService.addAttendeesToMeeting(newAttendeeIds, committeeId, meetingId, authentication.getName());
        return ResponseEntity.ok(new Response(newAttendees));
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
