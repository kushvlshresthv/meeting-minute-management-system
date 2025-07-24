package com.sep.mmms_backend.service;


import com.sep.mmms_backend.dto.MemberDetailsDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.testing_tools.TestDataHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

//@SpringBootTest(classes={LocalValidatorFactoryBean.class, MemberService.class, AppConfig.class})

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {

    @Mock
    private MemberRepository memberRepository;

//    @MockitoBean
//    private CommitteeRepository committeeRepository;
//
//    @MockitoBean
//    private EntityValidator entityValidator;

    @InjectMocks
    private MemberService memberService;

    @Nested
    @DisplayName("Tests for the getMemberDetails method")
    class GetMemberDetails {
        private final int memberId = 1;
        private final String username = "testUser";
        private Member member;
        //NOTE: committee that is associated with the above member
        private Committee committee;

        //NOTE: meeting attended by the above member
        private Meeting meeting;

        @BeforeEach
        void setUp() {
            TestDataHelper helper = new TestDataHelper();
            member = helper.getMember();
            committee = helper.getCommittee();
            meeting = helper.getMeeting();
        }

        @Test
        @DisplayName("specified memberId is not present in the database")
        void testMemberNotFound() {
            // Arrange
            when(memberRepository.findMemberById(memberId)).thenThrow(new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId));

            // Act & Assert
            MemberDoesNotExistException ex = assertThrows(MemberDoesNotExistException.class, () -> {
                memberService.getMemberDetails(memberId, username);
            });

            assertThat(ex.getMessage()).isEqualTo(ExceptionMessages.MEMBER_DOES_NOT_EXIST.toString());

           verify(memberRepository, times(1)).findMemberById(memberId);
        }

        @Test
        @DisplayName("member is not accessible to the user ie the 'createdBy' of the member stored in the database is not same as the supplied username")
        void testMemberNotAccessible() {
            // Arrange
            String differentUsername = "differentUser";
            member.setCreatedBy(differentUsername);
            when(memberRepository.findMemberById(memberId)).thenReturn(member);

            // Act & Assert
            IllegalOperationException ex = assertThrows(IllegalOperationException.class, () -> {
                memberService.getMemberDetails(memberId, username);
            });

            Assertions.assertThat(ex.getMessage()).isEqualTo(ExceptionMessages.MEMBER_NOT_ACCESSIBLE.toString());

            verify(memberRepository, times(1)).findMemberById(memberId);
        }

        @Test
        @DisplayName("attended meeting for a particular member is null")
        void testAttendedMeetingsNull() {
            // Arrange
            member.setAttendedMeetings(null);
            when(memberRepository.findMemberById(memberId)).thenReturn(member);

            // Act
            MemberDetailsDto result = memberService.getMemberDetails(memberId, username);

            // Assert
            assertThat(result).isNotNull();
            assertThat(memberId).isEqualTo(result.getMemberId());
            assertThat(member.getFirstName()).isEqualTo(result.getFirstName());
            assertThat(member.getLastName()).isEqualTo(result.getLastName());
            assertThat(result.getCommitteeWithMeetings().isEmpty()).isFalse();

            // Verify meeting attendance is false since attendedMeetings is null
            MemberDetailsDto.MeetingInfo meetingInfo = result.getCommitteeWithMeetings().getFirst().meetingInfos().getFirst();
            assertThat(meetingInfo.hasAttendedMeeting()).isFalse();

            verify(memberRepository, times(1)).findMemberById(memberId);
        }

        @Test
        @DisplayName("committee for a particular member is null")
        void testCommitteesNull() {
            // Arrange
            member.setMemberships(new HashSet<>());
            when(memberRepository.findMemberById(memberId)).thenReturn(member);

            // Act
            MemberDetailsDto result = memberService.getMemberDetails(memberId, username);

            // Assert
            assertThat(result).isNotNull();
            assertThat(memberId).isEqualTo(result.getMemberId());
            assertThat(member.getFirstName()).isEqualTo(result.getFirstName());
            assertThat(member.getLastName()).isEqualTo(result.getLastName());
            assertThat(result.getCommitteeWithMeetings().isEmpty()).isTrue();

            verify(memberRepository, times(1)).findMemberById(memberId);
        }

        @Test
        @DisplayName("success case")
        void testSuccessCase() {
            // Arrange
            when(memberRepository.findMemberById(memberId)).thenReturn(member);

            // Act
            MemberDetailsDto result = memberService.getMemberDetails(memberId, username);

            // Assert
            assertThat(result).isNotNull();
            assertThat(memberId).isEqualTo(result.getMemberId());
            assertThat(member.getFirstName()).isEqualTo(result.getFirstName());
            assertThat(member.getLastName()).isEqualTo(result.getLastName());
            assertThat(member.getInstitution()).isEqualTo(result.getInstitution());
            assertThat(member.getPost()).isEqualTo(result.getPost());
            assertThat(member.getQualification()).isEqualTo(result.getQualification());

            // Check committee info
            assertThat(result.getCommitteeWithMeetings().isEmpty()).isFalse();
            MemberDetailsDto.CommitteeInfo committeeInfo = result.getCommitteeWithMeetings().getFirst().committeeInfo();
            assertThat(committeeInfo.id()).isEqualTo(1);
            assertThat(committee.getName()).isEqualTo(committeeInfo.committeeName());
            assertThat(committee.getDescription()).isEqualTo(committeeInfo.committeeDescription());
            assertThat("Member").isEqualTo(committeeInfo.role());

            // Check meeting info
            assertThat(result.getCommitteeWithMeetings().getFirst().meetingInfos().isEmpty()).isFalse();
            MemberDetailsDto.MeetingInfo meetingInfo = result.getCommitteeWithMeetings().getFirst().meetingInfos().getFirst();
            assertThat(meetingInfo.id()).isEqualTo(1);
            assertThat(meeting.getTitle()).isEqualTo(meetingInfo.meetingTitle());
            assertThat(meeting.getDescription()).isEqualTo(meetingInfo.meetingDescription());
            assertThat(meetingInfo.hasAttendedMeeting()).isTrue();

            verify(memberRepository, times(1)).findMemberById(memberId);
        }
    }
}
