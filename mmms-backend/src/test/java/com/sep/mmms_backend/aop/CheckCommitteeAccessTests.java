package com.sep.mmms_backend.aop;

import com.sep.mmms_backend.aop.implementations.CheckCommitteeAccessAspect;
import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MeetingService;
import com.sep.mmms_backend.testing_tools.TestDataHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckCommitteeAccessTests {
   /*
        Perform unit tests for CheckCommmitteeAccessAspect method
        Create/Mock the join point with Committee and Meeting

        1. test when checkCommitteeAccess.shouldValidateMeeting = false
            test when the committee does not exist
            test when the committee is not accessible
        2. test when checkCommitteeAccess.shouldValidateMeeting = true(validate both)
            test when the meeting does not exist
            test when the meeting is not accessiblell
    */

    @Mock
    private CommitteeService committeeService;

    @Mock
    private MeetingService meetingService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private CheckCommitteeAccess checkCommitteeAccess;

    @Mock
    private RequestAttributes requestAttributes;

    @InjectMocks
    private CheckCommitteeAccessAspect aspect;

    private final int committeeId = 1;
    private final String username = "testUser";
    private final int meetingId = 1;

    private Committee committee;
    private Meeting meeting;

    @BeforeEach
    void setUp() {
        // Create test data
        TestDataHelper helper = new TestDataHelper();
        committee = helper.getCommittee();
        meeting = helper.getMeeting();

        // Set up joinPoint
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        // Set up RequestContextHolder
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Nested
    @DisplayName("Tests when shouldValidateMeeting = false")
    class WhenShouldValidateMeetingIsFalse {

        @BeforeEach
        void setUp() {
            // Set up method signature
            when(methodSignature.getParameterNames()).thenReturn(new String[]{"committeeId", "username"});
            when(joinPoint.getArgs()).thenReturn(new Object[]{committeeId, username});

            // Set shouldValidateMeeting to false
            when(checkCommitteeAccess.shouldValidateMeeting()).thenReturn(false);
        }


        @Test
        @DisplayName("Should throw CommitteeDoesNotExistException when committee does not exist")
        void testCommitteeDoesNotExist() {
            // Arrange
            when(committeeService.findCommitteeByIdNoException(committeeId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> aspect.checkCommitteeAccess(joinPoint, checkCommitteeAccess))
                    .isInstanceOf(CommitteeDoesNotExistException.class)
                    .hasMessageContaining(ExceptionMessages.COMMITTEE_DOES_NOT_EXIST.toString());
        }

        @Test
        @DisplayName("Should throw IllegalOperationException when committee is not accessible")
        void testCommitteeNotAccessible() {
            // Arrange
            AppUser differentUser = new AppUser();
            differentUser.setUsername("differentUser");
            committee.setCreatedBy(differentUser);

            when(committeeService.findCommitteeByIdNoException(committeeId)).thenReturn(Optional.of(committee));

            // Act & Assert
            assertThatThrownBy(() -> aspect.checkCommitteeAccess(joinPoint, checkCommitteeAccess))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining(ExceptionMessages.COMMITTEE_NOT_ACCESSIBLE.toString());
        }

        @Test
        @DisplayName("Should not throw exception when committee exists and is accessible")
        void testCommitteeExistsAndIsAccessible() {
            // Arrange
            when(committeeService.findCommitteeByIdNoException(committeeId)).thenReturn(Optional.of(committee));

            // Act & Assert
            assertThatCode(() -> aspect.checkCommitteeAccess(joinPoint, checkCommitteeAccess))
                    .doesNotThrowAnyException();
        }

    }


    @Nested
    @DisplayName("Tests when shouldValidateMeeting = true")
    class WhenShouldValidateMeetingIsTrue {

        @BeforeEach
        void setUp() {
            // Set up method signature
            when(methodSignature.getParameterNames()).thenReturn(new String[]{"committeeId", "username", "meetingId"});
            when(joinPoint.getArgs()).thenReturn(new Object[]{committeeId, username, meetingId});

            // Set shouldValidateMeeting to true
            when(checkCommitteeAccess.shouldValidateMeeting()).thenReturn(true);

            // Set up committee service
            when(committeeService.findCommitteeByIdNoException(committeeId)).thenReturn(Optional.of(committee));
        }

        @Test
        @DisplayName("Should throw MeetingDoesNotExistException when meeting does not exist")
        void testMeetingDoesNotExist() {
            // Arrange
            when(meetingService.findMeetingByIdNoException(meetingId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> aspect.checkCommitteeAccess(joinPoint, checkCommitteeAccess))
                    .isInstanceOf(MeetingDoesNotExistException.class)
                    .hasMessageContaining(ExceptionMessages.MEETING_DOES_NOT_EXIST.toString());
        }

        @Test
        @DisplayName("Should throw IllegalOperationException when meeting is not in committee")
        void testMeetingNotInCommittee() {
            // Arrange
            Meeting differentMeeting = new Meeting();
            differentMeeting.setUuid("aaetae");

            when(meetingService.findMeetingByIdNoException(meetingId)).thenReturn(Optional.of(differentMeeting));

            // Act & Assert
            assertThatThrownBy(() -> aspect.checkCommitteeAccess(joinPoint, checkCommitteeAccess))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining(ExceptionMessages.MEETING_NOT_IN_COMMITTEE.toString());
        }

        @Test
        @DisplayName("Should not throw exception when meeting exists and is in committee")
        void testMeetingExistsAndIsInCommittee() {
            // Arrange
            when(meetingService.findMeetingByIdNoException(meetingId)).thenReturn(Optional.of(meeting));

            // Act & Assert
            assertThatCode(() -> aspect.checkCommitteeAccess(joinPoint, checkCommitteeAccess))
                    .doesNotThrowAnyException();
        }
    }
}
