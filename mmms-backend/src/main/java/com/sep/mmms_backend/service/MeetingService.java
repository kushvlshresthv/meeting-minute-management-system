package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.exceptions.MemberNotInCommitteeException;
import com.sep.mmms_backend.repository.MeetingRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final CommitteeService committeeService;
    private final MemberService memberService;
    private final EntityValidator entityValidator;

    public MeetingService(MeetingRepository meetingRepository, CommitteeService committeeService, MemberService memberService, EntityValidator entityValidator) {
        this.meetingRepository = meetingRepository;
        this.committeeService = committeeService;
        this.memberService = memberService;
        this.entityValidator = entityValidator;
    }

    public Meeting saveNewMeeting(Meeting meeting, int committeeId, String username) {
        this.entityValidator.validate(meeting);
        Committee committee = committeeService.findCommitteeById(committeeId, username);

        //populating the meetings
        if(!meeting.getAttendees().isEmpty()) {
            List<Integer> attendeeMemberIds = meeting.getAttendees().stream().map(Member::getMemberId).toList();
            List<Member> attendees = memberService.findAllById(attendeeMemberIds);

            if(attendees.size() != attendeeMemberIds.size()) {
                List<Integer> foundMembers = attendees.stream().map(Member::getMemberId).toList();
                List<Integer> missingMembers = attendeeMemberIds.stream().filter(memberId -> !foundMembers.contains(memberId)).toList();
                throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, missingMembers.getFirst());
            }

            List<Member> committeeMemberList = committee.getMemberships().stream().map(CommitteeMembership::getMember).toList();
            for(Member attendee: attendees) {
                if(!committeeMemberList.contains(attendee)) {
                    throw new MemberNotInCommitteeException(ExceptionMessages.MEMBER_NOT_IN_COMMITTEE, attendee.getMemberId(), committeeId);
                }
            }
            meeting.setAttendees(attendees);
        }

        meeting.setCommittee(committee);
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Meeting meeting) {
        if(meetingRepository.existsById(meeting.getMeetingId())) {
            Meeting existingMeeting = meetingRepository.findMeetingByMeetingId(meeting.getMeetingId());
            //update the data
            existingMeeting.setMeetingTitle(meeting.getMeetingTitle());
            existingMeeting.setMeetingDescription(meeting.getMeetingDescription());
            existingMeeting.setMeetingHeldDate(meeting.getMeetingHeldDate());

            return meetingRepository.save(existingMeeting);
        }
        throw new MeetingDoesNotExistException(ExceptionMessages.MEETING_DOES_NOT_EXIST);
    }
}
