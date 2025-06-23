package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("api")
public class MeetingController {
   MeetingService meetingService;

   MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
   }

    @PostMapping("/createMeeting")
    public ResponseEntity<Response> createMeeting(@RequestBody @Valid Meeting meeting, Errors errors) {

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

        Meeting savedMeeting = this.meetingService.saveNewMeeting(meeting);

        return ResponseEntity.ok().body(new Response("Meeting created successfully"));
    }
}
