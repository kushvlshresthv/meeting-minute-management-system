package com.sep.mmms_backend.service;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeetingMinutePreparationService {
    private final MeetingService meetingService;
    private final CommitteeService committeeService;
    private final MemberService memberService;

    public MeetingMinutePreparationService(MeetingService meetingService, CommitteeService committeeService, MemberService memberService) {
        this.meetingService = meetingService;
        this.committeeService = committeeService;
        this.memberService = memberService;
    }

    /**
     * Prepares all necessary data for the meeting minute view.
     *
     * @param committeeId The ID of the committee.
     * @param meetingId The ID of the meeting.
     * @param username The username of the authenticated user.
     * @return A map containing all data attributes for the Thymeleaf model.
     * @throws CommitteeDoesNotExistException If the committee is not found in the database.
     * @throws MeetingDoesNotExistException If the mmeting is not found in the database.
     * @throws IllegalOperationException If the user does not have access to the committee/meeting.
     *
     */
    public Map<String, Object> prepareMeetingMinuteData(int committeeId, int meetingId, String username) {

        //1. validate committee is accessible by the user
        Committee committee = committeeService.findByIdNoException(committeeId).orElseThrow(CommitteeDoesNotExistException::new);

        if (!committee.getCreatedBy().getUsername().equals(username)) {
            throw new IllegalOperationException();
        }

        // 2. Validate Meeting and its association with Committee
        Meeting meeting = meetingService.findMeetingByIdNoException(meetingId)
                .orElseThrow(MeetingDoesNotExistException::new);

        if (!committee.getMeetings().contains(meeting)) {
            throw new IllegalOperationException();
        }

        // 3. Populate Data Map
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("meeting", meeting);
        modelData.put("meetingHeldDate", meeting.getHeldDate());
        modelData.put("meetingHeldDay", meeting.getHeldDate().getDayOfWeek().toString());
        modelData.put("partOfDay", getPartOfDay(meeting.getHeldTime()));
        modelData.put("meetingHeldTime", meeting.getHeldTime());
        modelData.put("meetingHeldPlace", meeting.getHeldPlace());
        modelData.put("meetingTitle", meeting.getTitle());
        modelData.put("committeeName", meeting.getCommittee().getName());
        modelData.put("coordinatorFullName", formatCoordinatorFullName(meeting.getCoordinator()));
        modelData.put("membershipsOfAttendees", getSortedAttendeesMemberships(meeting, committeeId));
        modelData.put("decisions", meeting.getDecisions());

        return modelData;
    }

        private String getPartOfDay(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 5 && hour < 12) {
            return "Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Evening";
        } else {
            return "Night";
        }
    }

    private String formatCoordinatorFullName(Member coordinator) {
        return coordinator.getFirstName() + " " + coordinator.getLastName();
    }

    private List<CommitteeMembership> getSortedAttendeesMemberships(Meeting meeting, int committeeId) {
        List<CommitteeMembership> membershipsOfAttendees = meeting.getAttendees().stream()
                .map(attendee -> {
                    CommitteeMembership membership = memberService.getMembership(attendee, committeeId);
                    //we are sure that attendee is part of the committee because we have already checked that meeting is part of the committee and a member can only be an attendee to a meeting if both belong to the same committee


                    //Furthermore, the coordinator is automatically moved to an attendee of the meeting when a member is registered as a coordinator for a meeting.
                    if (attendee.getId() == meeting.getCoordinator().getId()) {
                        membership.setRole("Coordinator");
                    }
                    return membership;
                })
                .collect(Collectors.toList());

        sortMembershipByRole(membershipsOfAttendees);
        return membershipsOfAttendees;
    }

    /**
     * Sorts memberships object based on role in the order: 'coordinator -> member -> member_secretary -> invitee'
     */
    private void sortMembershipByRole(List<CommitteeMembership> memberships) {
        if (memberships == null || memberships.isEmpty()) {
            return;
        }
        Map<String, Integer> rolePriority = new HashMap<>();
        rolePriority.put("Coordinator", 1);
        rolePriority.put("Member", 2);
        rolePriority.put("Member_Secretary", 3);
        rolePriority.put("Invitee", 4);

        memberships.sort(Comparator.comparingInt(m -> rolePriority.getOrDefault(m.getRole(), Integer.MAX_VALUE)));
    }
}
