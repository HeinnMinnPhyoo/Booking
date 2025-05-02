package com.bookingapp.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduleResponse {
    private Long id;
    private String className;
    private String countryCode;
    private int requiredCredits;
    private int maxSlots;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
