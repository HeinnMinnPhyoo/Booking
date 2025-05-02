package com.bookingapp.entity.schedule;

import com.bookingapp.entity.classbooking.ClassBooking;
import com.bookingapp.entity.waitlist.Waitlist;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    private String countryCode;

    private int requiredCredits;

    private int maxSlots;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ClassBooking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Waitlist> waitlist = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getRequiredCredits() {
        return requiredCredits;
    }

    public void setRequiredCredits(int requiredCredits) {
        this.requiredCredits = requiredCredits;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<ClassBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<ClassBooking> bookings) {
        this.bookings = bookings;
    }

    public List<Waitlist> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(List<Waitlist> waitlist) {
        this.waitlist = waitlist;
    }
}
