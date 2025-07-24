package com.sep.mmms_backend.testing_tools;


import com.sep.mmms_backend.databuilder.*;
import com.sep.mmms_backend.entity.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
public class TestDataHelper {
    AppUser appUser;
    Committee committee;
    Member member;
    Meeting meeting;
    Set<CommitteeMembership> memberships;


    /*
        the order in which the function is called is very important.
     */
    public TestDataHelper() {
        createMemberships();
        createValidAppUser();
        createValidCommittee();
        createValidMember();
        createValidMeeting();
    }

    public void createValidAppUser() {
        appUser = AppUserBuilder.builder().build();
        appUser.setUid(1);
    }

    public void createValidCommittee() {
        committee = CommitteeBuilder.builder().withCreatedBy(appUser).build();
        committee.setCreatedDate(LocalDate.now());
        committee.setModifiedBy(appUser.getUsername());
        committee.setModifiedDate(LocalDate.now());
        committee.setId(1);
        memberships.forEach(membership->membership.setCommittee(committee));
        committee.setMemberships(memberships);


        
        //setting the committees in app user
        List<Committee> myCommittees = new ArrayList<>();
        myCommittees.add(committee);
        appUser.setMyCommittees(myCommittees);
    }

    public void createValidMember() {
        member = MemberBuilder.builder().build();
        memberships.forEach(membership->membership.setMember(member));
        member.setMemberships(memberships);
        member.setId(1);
        member.setCreatedBy(appUser.getUsername());
        member.setModifiedBy(appUser.getUsername());
        member.setModifiedDate(LocalDate.now());
        member.setCreatedDate(LocalDate.now());
    }

    public void createValidMeeting () {
        Decision decision = DecisionBuilder.builder().build();
        List<Decision> decisions = new ArrayList<>();
        decisions.add(decision);

        Set<Member> attendees = new HashSet<>();
        attendees.add(member);

        meeting = MeetingBuilder.builder().withCommittee(committee).withCoordinator(member).withDecisions(decisions).withAttendees(attendees).build();

        meeting.setId(1);
        meeting.setCreatedBy(appUser.getUsername());
        meeting.setUpdatedBy(appUser.getUsername());
        meeting.setUpdatedDate(LocalDate.now());
        meeting.setCreatedDate(LocalDate.now());


        //set 'meetings' field in the committee object
        Set<Meeting> meetings = new HashSet<>();
        meetings.add(meeting);
        committee.setMeetings(meetings);


        //set attended meetings field in the member object
        Set<Meeting> attendedMeetings = new HashSet<>();
        attendedMeetings.add(meeting);
        member.setAttendedMeetings(attendedMeetings);
    }

    public void createMemberships() {
        memberships =  new HashSet<>();
        CommitteeMembership membership = CommitteeMembershipBuilder.builder().withRole("Member").build();
        memberships.add(membership);
    }
}

