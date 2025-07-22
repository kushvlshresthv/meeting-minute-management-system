package com.sep.mmms_backend.repository;

import com.sep.mmms_backend.component.AuditorAwareImpl;
import com.sep.mmms_backend.config.JpaAuditingConfiguration;
import com.sep.mmms_backend.databuilder.*;
import com.sep.mmms_backend.entity.*;
import com.sep.mmms_backend.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@DataJpaTest(properties = {
    "spring.jpa.properties.jakarta.persistence.validation.mode=none",
})

@Import({JpaAuditingConfiguration.class, AuditorAwareImpl.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @MockitoBean
    AppUserService appUserService;

    Meeting meeting;

    @BeforeEach
    public void init() {
        AppUser testUser = AppUserBuilder.builder().withUsername("testUser").build();
        testUser = appUserRepository.save(testUser);

        Committee committee = CommitteeBuilder.builder().withCreatedBy(testUser).build();
        committee = committeeRepository.save(committee);

        CommitteeMembership membership = CommitteeMembershipBuilder.builder().withCommittee(committee).build();
        Set<CommitteeMembership> memberships = new HashSet<>();
        memberships.add(membership);

        Member member = MemberBuilder.builder().withMemberships(memberships).build();
        member = memberRepository.save(member);


        Set<Member> attendees = new HashSet<>();
        attendees.add(member);

        Decision decision = DecisionBuilder.builder().withDecisionText("Decision").build();
        List<Decision> decisions = new LinkedList<>();
        decisions.add(decision);

        meeting = MeetingBuilder.builder().withCoordinator(member).withAttendees(attendees).withCommittee(committee).withDecisions(decisions).build();
//        System.out.println("SAVING MEETING NOW::::::::");
//        newMeeting = new Meeting();
//        newMeeting.setTitle("title");
//        newMeeting.setHeldDate(LocalDate.now());
//        newMeeting.setHeldTime(LocalTime.now());
//        newMeeting.setHeldPlace("Place");
//        newMeeting.setCommittee(committee);
//        newMeeting.setCoordinator(member);
//        for(Decision d: decisions) {
//            d.setMeeting(newMeeting);
//        }
//        newMeeting.setDecisions(decisions);
//        meeting.setCommittee(committee);
//        meeting.setCoordinator(member);
//        for(Decision d: decisions) {
//           d.setMeeting(meeting);
//        }
//        meeting.setDecisions(decisions);
    }

    Meeting newMeeting;


   @Nested
   @DisplayName("When a new meeting is created")
   @WithMockUser("testUser")
   public class CreateMeetingTests {
       @Test
       @DisplayName("auditing fields should be populated")
       public void saveMeeting_ShouldSetAuditingFields() {

           Meeting savedMeeting = meetingRepository.save(meeting);

           Meeting foundMeeting = meetingRepository.findById(savedMeeting.getId()).orElse(null);
           Assertions.assertThat(foundMeeting).isNotNull();
           Meeting m =  foundMeeting.getDecisions().getFirst().getMeeting();

           //a) verify that the @CreatedBy and @UpdatedBy fields are set
           Assertions.assertThat(foundMeeting.getCreatedBy()).isNotNull();
           Assertions.assertThat(foundMeeting.getUpdatedBy()).isNotNull();
           Assertions.assertThat(foundMeeting.getCreatedBy()).isEqualTo("testUser");
           Assertions.assertThat(foundMeeting.getUpdatedBy()).isEqualTo("testUser");

           //b) Verify that the @CreatedDate and @LostModifiedDate fields are set
           Assertions.assertThat(foundMeeting.getCreatedDate()).isNotNull();
           Assertions.assertThat(foundMeeting.getUpdatedDate()).isNotNull();
           Assertions.assertThat(foundMeeting.getCreatedDate()).isEqualTo(LocalDate.now());
           Assertions.assertThat(foundMeeting.getUpdatedDate()).isEqualTo(LocalDate.now());
       }
   }
}



//security context holder is set because it is used by the JPAAuditing
//   static {
//       SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//       List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//
//       UserDetails userDetails = org.springframework.security.core.userdetails.User
//               .withUsername("testUser")
//               .password("") // Password can be empty as it's not used for this type of authentication
//               .authorities(authorities)
//               .accountExpired(false)
//               .accountLocked(false)
//               .credentialsExpired(false)
//               .disabled(false)
//               .build();
//
//       UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//               userDetails,
//               null,
//               userDetails.getAuthorities()
//       );
//
//       securityContext.setAuthentication(authentication);
//       SecurityContextHolder.setContext(securityContext);
//   }



//   @Nested
//   @DisplayName("When an existing meeting is updated")
//   class UpdateMeetingTests {
//        @Test
//        @DisplayName("should only update the 'updatedBy' and 'updatedDate' fields")
//        public void updateMeeting_ShouldUpdateAuditingFields() {
//            meetingRepository.save(meeting);
//
//            Meeting initialSavedMeeting = meetingRepository.findById(meeting.getId()).orElse(null);
//            Assertions.assertThat(initialSavedMeeting).isNotNull();
//
//
//            //changing the SecurityContext>Authentication's username
//            //this also changes the 'savedTestUser'
//            setUpSecurityContext("updatedUsername");
//            initialSavedMeeting.setTitle("updateMeeting");
//            meetingRepository.save(initialSavedMeeting);
//
//            //check if the Meeting is updated, not resaved
//            Assertions.assertThat(meetingRepository.count()).isEqualTo(1);
//
//            Meeting updatedMeeting = meetingRepository.findById(initialSavedMeeting.getId()).orElse(null);
//            Assertions.assertThat(updatedMeeting).isNotNull();
//
//            //CreatedBy and CreatedDate should not change
//            Assertions.assertThat(updatedMeeting.getCreatedBy()).isEqualTo(initialSavedMeeting.getCreatedBy());
//            Assertions.assertThat(updatedMeeting.getCreatedDate()).isEqualTo(initialSavedMeeting.getCreatedDate());
//
//            //UpdateBy field should be updated
//            Assertions.assertThat(updatedMeeting.getUpdatedBy()).isEqualTo(savedTestUser.getUsername());
//
//            Assertions.assertThat(updatedMeeting.getUpdatedBy()).isEqualTo(savedTestUser.getUsername());
//
//            log.info("CreatedBy: {}", updatedMeeting.getCreatedBy());
//            log.info("Updated By field when the Meeting was created: {} ", updatedMeeting.getUpdatedBy());
//            log.info("Updated By field when the Meeting was resaved by different user: {}", updatedMeeting.getUpdatedBy());
//        }
//   }
