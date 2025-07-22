package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MeetingRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;
    private final EntityValidator entityValidator;
    private final CommitteeRepository committeeRepository;

    public MeetingService(MeetingRepository meetingRepository, CommitteeRepository committeeRepository, MemberRepository memberRepository, EntityValidator entityValidator) {
        this.meetingRepository = meetingRepository;
        this.entityValidator = entityValidator;
        this.committeeRepository = committeeRepository;
        this.memberRepository = memberRepository;
    }

    @CheckCommitteeAccess
    public Meeting saveNewMeeting(Meeting meeting, int committeeId, String username) {
        this.entityValidator.validate(meeting);
        Committee committee = committeeRepository.findCommitteeById(committeeId);
        Member coordinator = memberRepository.findMemberById(meeting.getCoordinator().getId());
        List<Member> committeeMemberList = committee.getMemberships().stream().map(CommitteeMembership::getMember).toList();


        //populating the meetings
        if(meeting.getAttendees() != null && !meeting.getAttendees().isEmpty()) {
            List<Integer> attendeeMemberIds = meeting.getAttendees().stream().map(Member::getId).toList();
            Set<Member> attendees = new HashSet<>(memberRepository.findAllMembersById(attendeeMemberIds));

            //when some attendee Ids are missing from the datbase
            if(attendees.size() != attendeeMemberIds.size()) {
                List<Integer> foundMembers = attendees.stream().map(Member::getId).toList();
                List<Integer> missingMembers = attendeeMemberIds.stream().filter(memberId -> !foundMembers.contains(memberId)).toList();
                throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, missingMembers.getFirst());
            }

            //check if all the attendees are in the committee
            for(Member attendee: attendees) {
                if(!committeeMemberList.contains(attendee)) {
                    throw new MemberNotInCommitteeException(ExceptionMessages.MEMBER_NOT_IN_COMMITTEE, attendee.getId(), committeeId);
                }
            }

            //NOTE: don't do getAttendees().addAll() because the attendees from the request body still lives in that container
            meeting.setAttendees(attendees);
        }

        //check if the coordinator is in the committee
        if(!committeeMemberList.contains(coordinator)) {
            throw new MemberNotInCommitteeException(ExceptionMessages.MEMBER_NOT_IN_COMMITTEE, coordinator.getId(), committeeId) ;
        }

        //ASSERTION: coordinator is also an attendee
        if(meeting.getAttendees() == null) {
            meeting.setAttendees(new HashSet<>());
        }
        meeting.getAttendees().add(coordinator);
        meeting.setCoordinator(coordinator);

        //add the current meeting instance to all the decisions
        //this is impoprtant because meeting is in the inverse side of the relationship
        meeting.getDecisions().forEach(decision->decision.setMeeting(meeting));

        meeting.setCommittee(committee);
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Meeting meeting) {
        if(meetingRepository.existsById(meeting.getId())) {
            Meeting existingMeeting = meetingRepository.findMeetingById(meeting.getId());
            //update the data
            existingMeeting.setTitle(meeting.getTitle());
            existingMeeting.setDescription(meeting.getDescription());
            existingMeeting.setHeldDate(meeting.getHeldDate());

            return meetingRepository.save(existingMeeting);
        }
        throw new MeetingDoesNotExistException(ExceptionMessages.MEETING_DOES_NOT_EXIST, meeting.getId());
    }


    /**
     *
     * @param newAttendeeIds the list of ids(of new attendees)
     * @param committeeId the committeeId to which the meeting belongs to
     * @param meetingId the meetingId of the meeting
     * @param username the name of the current user
     * @return list of saved attendees
     * NOTE: if the newAttendees list has some members which are already attendees, they are not re-added
     */

    @CheckCommitteeAccess(shouldValidateMeeting=true)
    @Transactional
    public Set<Member> addAttendeesToMeeting(Set<Integer> newAttendeeIds, int committeeId, int meetingId, String username) {

        //The committee and meeting is fetched and stored in the RCH by @CheckCommitteeAccess

        Committee committee = (Committee) RequestContextHolder.currentRequestAttributes().getAttribute("committee", RequestAttributes.SCOPE_REQUEST);
        Meeting meeting = (Meeting) RequestContextHolder.currentRequestAttributes().getAttribute("meeting", RequestAttributes.SCOPE_REQUEST);


        //1. find the members exists in the database and part of the committee
        //ASSERTION: attendees and meeting must belong to the same committee
        Set<Member> validNewAttendees = memberRepository.findExistingMembersInCommittee(newAttendeeIds, committeeId);

        if(validNewAttendees.size() != newAttendeeIds.size()) {
            Set<Integer> foundIds = validNewAttendees.stream()
                    .map(Member::getId)
                    .collect(Collectors.toSet());

            Set<Integer> missingOrInvalidIds = new HashSet<>(newAttendeeIds);
            missingOrInvalidIds.removeAll(foundIds);


            throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, missingOrInvalidIds);
        }

        meeting.getAttendees().addAll(validNewAttendees);
        meetingRepository.save(meeting);

        return validNewAttendees;
    }


    public Meeting findMeetingById(int meetingId) {
        return meetingRepository.findById(meetingId).orElseThrow(() -> new MeetingDoesNotExistException(ExceptionMessages.MEETING_DOES_NOT_EXIST, meetingId));
    }

    public Optional<Meeting> findMeetingByIdNoException(int meetingId) {
        return meetingRepository.findById(meetingId);
    }

    @CheckCommitteeAccess(shouldValidateMeeting=true)
    public Meeting getMeetingDetails(int committeeId, int meetingId, String username) {
        Meeting meeting = this.findMeetingById(meetingId);
        return meeting;
    }
}
