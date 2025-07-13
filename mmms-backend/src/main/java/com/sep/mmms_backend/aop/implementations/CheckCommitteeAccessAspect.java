package com.sep.mmms_backend.aop.implementations;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CheckCommitteeAccessAspect {
    @Autowired
    private CommitteeService committeeService;

    @Autowired
    private MeetingService meetingService;

    @Before("@annotation(checkCommitteeAccess)")
    public void checkCommitteeAccess(JoinPoint joinPoint, CheckCommitteeAccess checkCommitteeAccess) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Object[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        int committeeId = 0;
        String username = "";
        int meetingId = 0;

        boolean flagFoundCommitteeId = false;
        boolean flagFoundUsername = false;
        boolean flagFoundMeetingId = false;

        for(int i =0 ; i<parameterNames.length; i++) {
            if("committeeId".equals(parameterNames[i])) {
                committeeId = (Integer)args[i];
                flagFoundCommitteeId = true;
            }
            if("username".equals(parameterNames[i])) {
                username = (String)args[i];
                flagFoundUsername = true;
            }

            if(checkCommitteeAccess.shouldValidateMeeting() && "meetingId".equals(parameterNames[i])) {
                meetingId = (int) args[i];
                flagFoundMeetingId = true;
            }
        }

        if(!flagFoundCommitteeId || !flagFoundUsername ||(!flagFoundMeetingId && checkCommitteeAccess.shouldValidateMeeting())) {
            log.error("Error in AOP: Some parameters not found in method {}", signature.getName());
            throw new IllegalOperationException();
        }


        final int comId = committeeId;
        //checking access for the committee
        Committee committee = committeeService.findCommitteeByIdNoException(committeeId).orElseThrow(()-> new CommitteeDoesNotExistException(ExceptionMessages.COMMITTEE_DOES_NOT_EXIST, comId));

        if(!committee.getCreatedBy().getUsername().equals(username)) {
            throw new IllegalOperationException(ExceptionMessages.COMMITTEE_NOT_ACCESSIBLE);
        }

        //checking access for the meeting
        if(checkCommitteeAccess.shouldValidateMeeting()) {
            final int meetId = meetingId;
            Meeting meeting = meetingService.findMeetingByIdNoException(meetingId).orElseThrow(()-> new MeetingDoesNotExistException(ExceptionMessages.MEETING_DOES_NOT_EXIST, meetId));

            if(!committee.getMeetings().contains(meeting)) {
                throw new IllegalOperationException(ExceptionMessages.MEETING_NOT_IN_COMMITTEE);
            }
        }
    }
}
