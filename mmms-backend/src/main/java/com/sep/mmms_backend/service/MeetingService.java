package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.repository.MeetingRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingService {

    MeetingRepository meetingRepository;
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting saveNewMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }
}
