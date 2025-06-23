package com.sep.mmms_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity


public class MeetingMinute {
    @Id
    @Column(name="meeting_minute_id")
    private int meetingMinuteId;
}

