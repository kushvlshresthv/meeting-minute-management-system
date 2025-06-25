package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.exceptions.MeetingAlreadyExistsException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
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

       //if meetingId is populated in the request body, then the operation should be 'save' not 'update'
       if(meeting.getMeetingId() != 0) {
           return ResponseEntity.badRequest().body(new Response(ResponseMessages.ROUTE_CREATE_MEETING_MISUSED));
       }

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
           Meeting savedMeeting = this.meetingService.saveNewMeeting(meeting);
        } catch(MeetingAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new Response(ResponseMessages.ROUTE_CREATE_MEETING_MISUSED));
        }

        return ResponseEntity.ok().body(new Response(ResponseMessages.MEETING_CREATION_SUCCESSFUL));
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
