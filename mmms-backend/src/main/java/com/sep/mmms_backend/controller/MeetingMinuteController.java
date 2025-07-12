package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.service.MeetingMinutePreparationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class MeetingMinuteController {

        private final MeetingMinutePreparationService meetingMinutePreparationService;
        public MeetingMinuteController(MeetingMinutePreparationService meetingMinutePreparationService) {
            this.meetingMinutePreparationService = meetingMinutePreparationService;
        }
        @GetMapping("api/previewMeetingMinute")
        public String holiday(Model model, @RequestParam int committeeId, @RequestParam int meetingId, @RequestParam(required=false) String lang, Authentication authentication) {
            try {
                String username = authentication.getName();
                Map<String, Object> meetingMinuteData = meetingMinutePreparationService.prepareMeetingMinuteData(committeeId, meetingId, username);

                // Add all prepared data to the model
                model.addAllAttributes(meetingMinuteData);

                if (lang != null && lang.equalsIgnoreCase("en")) {
                    return "meeting_minute_english";
                }
                return "meeting_minute_nepali";
            } catch (MeetingDoesNotExistException | CommitteeDoesNotExistException | IllegalOperationException e) {
                return "committee_not_accessible";
            }
        }
}
