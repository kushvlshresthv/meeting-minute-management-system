package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.MeetingAlreadyExistsException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.repository.MeetingRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingService {

    MeetingRepository meetingRepository;
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting saveNewMeeting(Meeting meeting) {
       //if the meeting with the given id, throw Meeting
       if(meetingRepository.existsById(meeting.getMeetingId())) {
           throw new MeetingAlreadyExistsException(ExceptionMessages.MEETING_ALREADY_EXISTS);
       }
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Meeting meeting) {
        if(meetingRepository.existsById(meeting.getMeetingId())) {
            Meeting existingMeeting = meetingRepository.findMeetingByMeetingId(meeting.getMeetingId());
            //update the data
            existingMeeting.setMeetingName(meeting.getMeetingName());
            existingMeeting.setMeetingDescription(meeting.getMeetingDescription());
            existingMeeting.setMeetingHeldDate(meeting.getMeetingHeldDate());

            return meetingRepository.save(existingMeeting);
        }
        throw new MeetingDoesNotExistException(ExceptionMessages.MEETING_DOES_NOT_EXIST);
    }
}
