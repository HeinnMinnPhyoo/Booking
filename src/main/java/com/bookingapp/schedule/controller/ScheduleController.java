package com.bookingapp.schedule.controller;

import com.bookingapp.schedule.dto.BookingRequest;
import com.bookingapp.schedule.dto.CheckInRequest;
import com.bookingapp.schedule.dto.ScheduleResponse;
import com.bookingapp.schedule.dto.WaitlistRequest;
import com.bookingapp.schedule.service.ScheduleService;
import com.bookingapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<ScheduleResponse> getSchedules(@RequestParam String country) {
        return scheduleService.getScheduleByCountry(country);
    }

    @PostMapping("/book")
    public String bookClass(@RequestBody @Valid BookingRequest request, Authentication auth) {
        Long userId = userService.getUserByEmail(auth.getName()).getId();
        return scheduleService.bookClass(userId, request.getScheduleId());
    }

    @PostMapping("/waitlist")
    public String waitlist(@RequestBody @Valid WaitlistRequest request, Authentication auth) {
        Long userId = userService.getUserByEmail(auth.getName()).getId();
        return scheduleService.addToWaitlist(userId, request.getScheduleId());
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestBody @Valid CheckInRequest request, Authentication auth) {
        Long userId = userService.getUserByEmail(auth.getName()).getId();
        return scheduleService.checkIn(userId, request.getScheduleId());
    }

}
