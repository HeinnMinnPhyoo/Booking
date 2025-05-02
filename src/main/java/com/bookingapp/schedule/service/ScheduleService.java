package com.bookingapp.schedule.service;

import com.bookingapp.entity.classbooking.ClassBooking;
import com.bookingapp.entity.enums.BookingStatus;
import com.bookingapp.entity.schedule.Schedule;
import com.bookingapp.entity.user.User;
import com.bookingapp.entity.userpackage.UserPackage;
import com.bookingapp.entity.waitlist.Waitlist;
import com.bookingapp.repository.classbooking.ClassBookingRepository;
import com.bookingapp.repository.schedule.ScheduleRepository;
import com.bookingapp.repository.user.UserRepository;
import com.bookingapp.repository.userpackage.UserPackageRepository;
import com.bookingapp.repository.waitlist.WaitlistRepository;
import com.bookingapp.schedule.dto.ScheduleResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ClassBookingRepository bookingRepository;
    @Autowired
    private UserPackageRepository userPackageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private WaitlistRepository waitlistRepository;

    public List<ScheduleResponse> getScheduleByCountry(String countryCode) {
        return scheduleRepository.findByCountryCode(countryCode).stream()
                .map(s -> new ScheduleResponse(
                        s.getId(), s.getClassName(), s.getCountryCode(),
                        s.getRequiredCredits(), s.getMaxSlots(),
                        s.getStartTime(), s.getEndTime()
                )).collect(Collectors.toList());
    }

    @Transactional
    public String bookClass(Long userId, Long scheduleId) {
        RLock lock = redissonClient.getLock("lock:schedule:" + scheduleId); // Unique lock per class
        boolean acquired = false;

        try {
            // Try to acquire lock within 5 seconds, auto-release after 10 seconds
            acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("The system is busy, please try again.");
            }

            // Fetch user and schedule
            Schedule schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<ClassBooking> userBookings = bookingRepository.findByUserIdAndStatus(userId, BookingStatus.BOOKED);
            for (ClassBooking cb : userBookings) {
                Schedule existing = cb.getSchedule();
                if (schedule.getStartTime().isBefore(existing.getEndTime()) &&
                        schedule.getEndTime().isAfter(existing.getStartTime())) {
                    throw new RuntimeException("This class overlaps with another you have already booked.");
                }
            }

            // Ensure no existing booking
            if (bookingRepository.findByUserAndSchedule(user, schedule).isPresent()) {
                throw new RuntimeException("Already booked this class.");
            }

            // Check available slots
            long bookedCount = bookingRepository.countByScheduleIdAndStatus(scheduleId, BookingStatus.BOOKED);
            if (bookedCount >= schedule.getMaxSlots()) {
                return "Class is full. (Waitlist logic will handle this)";
            }

            // Get valid user package
            LocalDateTime now = LocalDateTime.now();
            Optional<UserPackage> upOpt = userPackageRepository
                    .findTopByUserIdAndPack_CountryCodeAndExpiryDateAfterOrderByExpiryDateAsc(
                            userId, schedule.getCountryCode(), now);

            if (upOpt.isEmpty()) {
                throw new RuntimeException("No valid package found for this country.");
            }

            UserPackage userPackage = upOpt.get();
            if (userPackage.getRemainingCredits() < schedule.getRequiredCredits()) {
                throw new RuntimeException("Insufficient credits.");
            }

            // Deduct credits
            userPackage.setRemainingCredits(userPackage.getRemainingCredits() - schedule.getRequiredCredits());
            userPackageRepository.save(userPackage);

            // Save booking
            ClassBooking booking = new ClassBooking();
            booking.setUser(user);
            booking.setSchedule(schedule);
            booking.setBookedAt(now);
            booking.setStatus(BookingStatus.BOOKED);
            bookingRepository.save(booking);

            return "Booking confirmed!";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Booking was interrupted, try again.");
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public String cancelBooking(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassBooking booking = bookingRepository.findByUserAndSchedule(user, schedule)
                .orElseThrow(() -> new RuntimeException("No booking found"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(schedule.getStartTime().minusHours(4))) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            return "Cancelled. No refund due to < 4 hr policy.";
        }

        // Refund credit
        Optional<UserPackage> userPack = userPackageRepository
                .findTopByUserIdAndPack_CountryCodeAndExpiryDateAfterOrderByExpiryDateAsc(userId, schedule.getCountryCode(), now);

        if (userPack.isPresent()) {
            userPack.get().setRemainingCredits(userPack.get().getRemainingCredits() + schedule.getRequiredCredits());
            userPackageRepository.save(userPack.get());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // PROMOTE waitlist if class was full
        long bookedCount = bookingRepository.countByScheduleIdAndStatus(scheduleId, BookingStatus.BOOKED);
        if (bookedCount < schedule.getMaxSlots()) {
            List<Waitlist> waitlisted = waitlistRepository.findByScheduleIdOrderByAddedAtAsc(scheduleId);
            if (!waitlisted.isEmpty()) {
                Waitlist promoted = waitlisted.get(0);

                Optional<UserPackage> wp = userPackageRepository.findTopByUserIdAndPack_CountryCodeAndExpiryDateAfterOrderByExpiryDateAsc(
                        promoted.getUser().getId(), schedule.getCountryCode(), now);

                if (wp.isPresent() && wp.get().getRemainingCredits() >= schedule.getRequiredCredits()) {
                    wp.get().setRemainingCredits(wp.get().getRemainingCredits() - schedule.getRequiredCredits());
                    userPackageRepository.save(wp.get());

                    ClassBooking newBooking = new ClassBooking();
                    newBooking.setUser(promoted.getUser());
                    newBooking.setSchedule(schedule);
                    newBooking.setBookedAt(now);
                    newBooking.setStatus(BookingStatus.BOOKED);
                    bookingRepository.save(newBooking);

                    waitlistRepository.delete(promoted);
                }
            }
        }

        return "Booking cancelled and credit refunded.";
    }

    public String addToWaitlist(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Waitlist entry = new Waitlist();
        entry.setSchedule(schedule);
        entry.setUser(user);
        entry.setAddedAt(LocalDateTime.now());
        entry.setRefunded(false);

        waitlistRepository.save(entry);
        return "Added to waitlist.";
    }

    public String checkIn(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        ClassBooking booking = bookingRepository.findByUserAndSchedule(
                userRepository.findById(userId).orElseThrow(), schedule
        ).orElseThrow(() -> new RuntimeException("No booking"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(schedule.getStartTime().minusMinutes(10))) {
            return "You can check in 10 mins before class starts.";
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
        return "Checked in.";
    }

}
