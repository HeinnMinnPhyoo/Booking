package com.bookingapp.repository.classbooking;

import com.bookingapp.entity.classbooking.ClassBooking;
import com.bookingapp.entity.enums.BookingStatus;
import com.bookingapp.entity.schedule.Schedule;
import com.bookingapp.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    List<ClassBooking> findByScheduleId(Long scheduleId);
    Optional<ClassBooking> findByUserAndSchedule(User user, Schedule schedule);
    long countByScheduleIdAndStatus(Long scheduleId, BookingStatus status);
    List<ClassBooking> findByUserIdAndStatus(Long userId, BookingStatus status);
}
