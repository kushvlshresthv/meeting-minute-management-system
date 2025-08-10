package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.AgendaDto;
import com.sep.mmms_backend.dto.MeetingCreationDto;
import com.sep.mmms_backend.dto.MeetingUpdationDto;
import com.sep.mmms_backend.entity.*;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MeetingRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;
    private final EntityValidator entityValidator;

    public MeetingService(MeetingRepository meetingRepository, MemberRepository memberRepository, EntityValidator entityValidator) {
        this.meetingRepository = meetingRepository;
        this.entityValidator = entityValidator;
        this.memberRepository = memberRepository;
    }

    //TODO: Create Tests
    @CheckCommitteeAccess
    public Meeting saveNewMeeting(MeetingCreationDto meetingCreationDto, Committee committee , String username) {
        entityValidator.validate(meetingCreationDto);

        Meeting meeting = new Meeting();

        meeting.setCommittee(committee);
        meeting.setTitle(meetingCreationDto.getTitle());
        if(meetingCreationDto.getDescription() != null)
            meeting.setDescription(meetingCreationDto.getDescription());
        meeting.setHeldDate(meetingCreationDto.getHeldDate());
        meeting.setHeldTime(meetingCreationDto.getHeldTime());
        meeting.setHeldPlace(meetingCreationDto.getHeldPlace());
        meetingCreationDto.getDecisions().forEach(decisionString -> {
            Decision decision = new Decision();
            decision.setDecision(decisionString);
            meeting.addDecision(decision);
        });

        meetingCreationDto.getAgendas().forEach(agendaString -> {
            Agenda agenda = new Agenda();
            agenda.setAgenda(agendaString);
            meeting.addAgenda(agenda);
        });

        //populating the attendees
        List<Integer> requestedAttendees = meetingCreationDto.getAttendeeIds().stream().toList();
        if(!requestedAttendees.isEmpty()) {
            List<Member> foundMembers = memberRepository.findAccessibleMembersByIds(requestedAttendees, username);
            memberRepository.validateWhetherAllMembersAreFound(requestedAttendees, foundMembers);

            Set<Member> membersInCommittee = new LinkedHashSet<>();

            if(committee != null && committee.getMemberships()!= null)  {
                membersInCommittee = committee.getMemberships().stream().map(CommitteeMembership::getMember).collect(Collectors.toSet());
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

    //TODO: Create Tests

    /**
     * This updates meeting details + agendas and decisions
     * If the provided agenda id already exists for the meeting, it simply updates the agenda string
     * Else it saves the new agenda id along with the agenda string in the database
     * Same is the case for decisions
     */
    @Transactional
    public Meeting updateExistingMeetingDetails(MeetingUpdationDto newMeetingData, String username) {

        if(newMeetingData.getMeetingId() == null) {
            throw new IllegalOperationException("TODO: Exception (handle this exception)");
        }

        Meeting existingMeeting = meetingRepository.findMeetingById(newMeetingData.getMeetingId());

        if(!existingMeeting.getCreatedBy().equals(username)) {
            throw new MeetingNotAccessibleException(ExceptionMessages.MEETING_NOT_ACCESSIBLE, existingMeeting.getTitle());
        }

        if(newMeetingData.getTitle() != null)
            existingMeeting.setTitle(newMeetingData.getTitle());
        if(newMeetingData.getDescription() != null)
            existingMeeting.setDescription(newMeetingData.getDescription());
        if(newMeetingData.getHeldDate() != null)
            existingMeeting.setHeldDate(newMeetingData.getHeldDate());
        if(newMeetingData.getHeldTime() != null)
            existingMeeting.setHeldTime(newMeetingData.getHeldTime());
        if(newMeetingData.getHeldPlace() != null)
            existingMeeting.setHeldPlace(newMeetingData.getHeldPlace());


        Map<Integer, Agenda> existingAgendas = existingMeeting.getAgendas().stream().collect(Collectors.toMap(Agenda::getAgendaId, agenda->agenda));

        if(!newMeetingData.getAgendas().isEmpty()) {
            List<Agenda> updatedAgendas = new ArrayList<>();
            newMeetingData.getAgendas().forEach(newAgendaDto -> {
                if(newAgendaDto.getAgendaId() != null && existingAgendas.containsKey(newAgendaDto.getAgendaId())) {
                    if(newAgendaDto.getAgenda() != null && !newAgendaDto.getAgenda().isBlank()) {
                        existingAgendas.get(newAgendaDto.getAgendaId()).setAgenda(newAgendaDto.getAgenda());
                        updatedAgendas.add(existingAgendas.get(newAgendaDto.getAgendaId()));
                    }
                } else {
                    if(newAgendaDto.getAgenda() != null && !newAgendaDto.getAgenda().isBlank()) {
                        Agenda newAgenda = new Agenda();
                        newAgenda.setAgenda(newAgendaDto.getAgenda());
                        updatedAgendas.add(newAgenda);
                    }
                }
            });
            existingMeeting.getAgendas().clear();
            existingMeeting.addAllAgendas(updatedAgendas);
        }

        Map<Integer, Decision> existingDecisions = existingMeeting.getDecisions().stream().collect(Collectors.toMap(Decision::getDecisionId, decision->decision));

        if(!newMeetingData.getDecisions().isEmpty()) {
            List<Decision> updatedDecisions = new ArrayList<>();
            newMeetingData.getDecisions().forEach(newDecisionDto -> {
                if(newDecisionDto.getDecisionId() != null && existingDecisions.containsKey(newDecisionDto.getDecisionId())) {
                    if( newDecisionDto.getDecision() != null && !newDecisionDto.getDecision().isBlank()) {
                        existingDecisions.get(newDecisionDto.getDecisionId()).setDecision(newDecisionDto.getDecision());
                        updatedDecisions.add(existingDecisions.get(newDecisionDto.getDecisionId()));
                    }
                } else {
                    if(newDecisionDto.getDecision() != null && !newDecisionDto.getDecision().isBlank()) {
                        Decision newDecision = new Decision();
                        newDecision.setDecision(newDecisionDto.getDecision());
                        updatedDecisions.add(newDecision);
                    }
                }
            });
            existingMeeting.getDecisions().clear();
            existingMeeting.addAllDecisions(updatedDecisions);
        }

        return meetingRepository.save(existingMeeting);
    }





    /**
     *
     * @param newAttendeeIds the list of ids(of new attendees)
     * @param committee the committee to which the meeting belongs to
     * @param meeting the meeting of the meeting
     * @param username the name of the current user
     * NOTE: if the newAttendees list has some members which are already attendees, they are not re-added
     */

    @CheckCommitteeAccess(shouldValidateMeeting=true)
    @Transactional
    //TODO: Create Tests
    public void addAttendeesToMeeting(Set<Integer> newAttendeeIds, Committee committee, Meeting meeting, String username) {

        if (!meeting.getCommittee().getId().equals(committee.getId())) {
            throw new MeetingDoesNotBelongToCommittee(ExceptionMessages.MEETING_NOT_IN_COMMITTEE, meeting.getTitle(), committee.getName());
        }

        //find the members exists in the database and part of the committee and also accessible by the user
        List<Member> validNewAttendees = memberRepository.findExistingMembersInCommittee(newAttendeeIds, committee.getId(), username);

        //check whether the member is already an attendee to the meeting
        Set<Integer> existingAttendeeIds = meeting.getAttendees().stream().map(Member::getId).collect(Collectors.toSet());

        try {
            memberRepository.validateWhetherAllMembersAreFound(newAttendeeIds.stream().toList(), validNewAttendees);
        } catch(MemberDoesNotExistException ex) {
            throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST_OR_NOT_PART_OF_COMMITTEE, ex);
        }

        List<Member> membersToAdd = validNewAttendees.stream()
                .filter(member -> !existingAttendeeIds.contains(member.getId()))
                .toList();

        if (membersToAdd.isEmpty()) {
            throw new IllegalOperationException("Member alread an attendee to the meeting");
            //TODO: (Exception) handle this exception properly by returning which new attendee already exists
        }

        meeting.getAttendees().addAll(validNewAttendees);
        meetingRepository.save(meeting);
    }


    public Meeting findMeetingById(int meetingId) {
        return meetingRepository.findById(meetingId).orElseThrow(() -> new MeetingDoesNotExistException(ExceptionMessages.MEETING_DOES_NOT_EXIST, meetingId));
    }

    public Optional<Meeting> findMeetingByIdNoException(int meetingId) {
        return meetingRepository.findById(meetingId);
    }

    //TODO: Create Tests
    @CheckCommitteeAccess(shouldValidateMeeting=true)
    public Meeting getMeetingDetails(Committee committee, Meeting meeting, String username) {
        return meeting;
    }
}
