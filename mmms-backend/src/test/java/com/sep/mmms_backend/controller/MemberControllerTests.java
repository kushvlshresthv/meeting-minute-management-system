package com.sep.mmms_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.mmms_backend.config.AppConfig;
import com.sep.mmms_backend.config.SecurityConfiguration;
import com.sep.mmms_backend.dto.MemberDetailsDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.service.AppUserService;
import com.sep.mmms_backend.service.MemberService;
import com.sep.mmms_backend.testing_tools.SerializerDeserializer;
import com.sep.mmms_backend.testing_tools.TestDataHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = MemberController.class)
@Import({SecurityConfiguration.class, AppConfig.class})
@Slf4j
public class MemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private AppUserService appUserService;


    @Nested
    @DisplayName("Testing /getMemberDetails route")
    class GetMemberDetailsRoute {
        private final int memberId = 1;
        private final String username = "testUser";
        private Member member;
        private MemberDetailsDto memberDetailsDto;

        @BeforeEach
        void setUp() {
            TestDataHelper helper  = new TestDataHelper();
            Meeting meeting = helper.getMeeting();
            Committee committee = helper.getCommittee();
            member = helper.getMember();


            MemberDetailsDto.CommitteeInfo committeeInfo = new MemberDetailsDto.CommitteeInfo(
                    committee.getId(), committee.getName(), committee.getDescription(), "Member");

            MemberDetailsDto.MeetingInfo meetingInfo = new MemberDetailsDto.MeetingInfo(
                    meeting.getId(), meeting.getTitle(), meeting.getDescription(), true);

            List<MemberDetailsDto.MeetingInfo> meetingInfos = new ArrayList<>();
            meetingInfos.add(meetingInfo);

            MemberDetailsDto.CommitteeWithMeetings committeeWithMeetings =
                    new MemberDetailsDto.CommitteeWithMeetings(committeeInfo, meetingInfos);

            List<MemberDetailsDto.CommitteeWithMeetings> committeeWithMeetingsList = new ArrayList<>();
            committeeWithMeetingsList.add(committeeWithMeetings);

            memberDetailsDto = new MemberDetailsDto(member, committeeWithMeetingsList);

        }

        private Response performRequestAndGetResponse(String url, HttpStatus expectedStatus) throws Exception {
            MvcResult result = mockMvc.perform(get(url)).andReturn();

            int actualStatusCode = result.getResponse().getStatus();
            assertThat(actualStatusCode).isEqualTo(expectedStatus.value());

            return SerializerDeserializer.deserialize(result.getResponse().getContentAsString());
        }

        //1. Tests the case in which the member does not exist
        @Test
        @WithMockUser(username = "testUser")
        @DisplayName("Should return NOT_FOUND when member does not exist")
        void testMemberNotFound() throws Exception {
            // Arrange
            Mockito.when(memberService.getMemberDetails(anyInt(), anyString()))
                    .thenThrow(new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId));

            // Act
            Response response = performRequestAndGetResponse("/api/getMemberDetails?memberId=" + memberId, HttpStatus.BAD_REQUEST);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).contains(ExceptionMessages.MEMBER_DOES_NOT_EXIST.toString());

            // Verify
            Mockito.verify(memberService, Mockito.times(1)).getMemberDetails(memberId, username);
        }

        //2. Tests the case in which the member is not accessible by the current user
        @Test
        @WithMockUser(username = "testUser")
        @DisplayName("Should return FORBIDDEN when member is not accessible by current user")
        void testMemberNotAccessible() throws Exception {
            // Arrange
            Mockito.when(memberService.getMemberDetails(anyInt(), anyString()))
                    .thenThrow(new IllegalOperationException(ExceptionMessages.MEMBER_NOT_ACCESSIBLE));

            // Act
            Response response = performRequestAndGetResponse("/api/getMemberDetails?memberId=" + memberId, HttpStatus.BAD_REQUEST);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).contains(ExceptionMessages.MEMBER_NOT_ACCESSIBLE.toString());

            // Verify
            Mockito.verify(memberService, Mockito.times(1)).getMemberDetails(memberId, username);
        }

        //3. Tests the case success case
        @Test
        @WithMockUser(username = "testUser")
        @DisplayName("Should return OK with member details when successful")
        void testSuccessCase() throws Exception {
            // Arrange
            Mockito.when(memberService.getMemberDetails(memberId, username)).thenReturn(memberDetailsDto);

            // Act
            Response response = performRequestAndGetResponse("/api/getMemberDetails?memberId=" + memberId, HttpStatus.OK);

            // Assert
            assertThat(response).isNotNull();

            // Convert mainBody to MemberDetailsDto using ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            MemberDetailsDto result = objectMapper.convertValue(response.getMainBody(), MemberDetailsDto.class);

            // Verify member details
            assertThat(result.getMemberId()).isEqualTo(memberId);
            assertThat(result.getFirstName()).isEqualTo(member.getFirstName());
            assertThat(result.getLastName()).isEqualTo(member.getLastName());
            assertThat(result.getInstitution()).isEqualTo(member.getInstitution());
            assertThat(result.getPost()).isEqualTo(member.getPost());
            assertThat(result.getQualification()).isEqualTo(member.getQualification());

            // Verify committee and meeting info
            assertThat(result.getCommitteeWithMeetings()).isNotEmpty();

            // Verify
            Mockito.verify(memberService, Mockito.times(1)).getMemberDetails(memberId, username);
        }
    }
}
