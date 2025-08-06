package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.MeetingCreationDto;
import com.sep.mmms_backend.entity.*;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MeetingRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
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
    public Meeting saveNewMeeting(MeetingCreationDto meetingCreationDto, Committee committee , String username) {
        entityValidator.validate(meetingCreationDto);

        Meeting meeting = new Meeting();
        meeting.setCommittee(committee);
        meeting.setTitle(meetingCreationDto.getTitle());
        meeting.setDescription(meetingCreationDto.getDescription());
        meeting.setHeldDate(meetingCreationDto.getHeldDate());
        meeting.setHeldTime(meetingCreationDto.getHeldTime());
        meeting.setHeldPlace(meetingCreationDto.getHeldPlace());
        meetingCreationDto.getDecisions().forEach(decisionString -> {
            Decision decision = new Decision();
            decision.setDecision(decisionString);
            meeting.addDecision(decision);
        });

        //populating the attendees
        Set<Integer> requestedAttendees = meetingCreationDto.getAttendees();
        if(!requestedAttendees.isEmpty()) {
            List<Member> foundMembers = memberRepository.findAndValidateMembers(requestedAttendees);

            List<Member> membersInCommittee = new LinkedList<>();

            if(committee != null && committee.getMemberships()!= null)  {
                membersInCommittee = committee.getMemberships().stream().map(CommitteeMembership::getMember).toList();
            }

            //check that the found members belong to the committee
            for(Member foundMember: foundMembers) {
                if(!membersInCommittee.contains(foundMember)) {
                    throw new MemberNotInCommitteeException(ExceptionMessages.MEMBER_NOT_IN_COMMITTEE, foundMember.getId(), committee.getId());
                }
            }
            meeting.setAttendees(foundMembers);
        }

        return meetingRepository.save(meeting);
    }

    //This methods needs to be updated
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
     * @param committee the committee to which the meeting belongs to
     * @param meeting the meeting of the meeting
     * @param username the name of the current user
     * @return set of saved attendees
     * NOTE: if the newAttendees list has some members which are already attendees, they are not re-added
     */

    @CheckCommitteeAccess(shouldValidateMeeting=true)
    @Transactional
    public Set<Member> addAttendeesToMeeting(Set<Integer> newAttendeeIds, Committee committee, Meeting meeting, String username) {

        //1. find the members exists in the database and part of the committee
        //ASSERTION: attendees and meeting must belong to the same committee
        Set<Member> validNewAttendees = memberRepository.findExistingMembersInCommittee(newAttendeeIds, committee.getId());

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
    public Meeting getMeetingDetails(Committee committee, Meeting meeting, String username) {
        return meeting;
    }
}
