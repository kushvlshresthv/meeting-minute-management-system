package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.service.MeetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MeetingMinuteController {
        private final MeetingService meetingService;

        public MeetingMinuteController(MeetingService meetingService) {
            this.meetingService = meetingService;
        }
        @GetMapping("/previewMeetingMinute")
        public String holiday(Model model, @RequestParam int committeeId, @RequestParam int meetingId) {
            Meeting meeting = meetingService.getMeetingById(meetingId);
            model.addAttribute("meeting", meeting);
            model.addAttribute("committee", meeting.getCommittee());
            model.addAttribute("attendees", meeting.getAttendees());
            return "meeting_minute";
        }

}
