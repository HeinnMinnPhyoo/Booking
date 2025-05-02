package com.bookingapp.schedule;

import com.bookingapp.entity.schedule.Schedule;
import com.bookingapp.entity.waitlist.Waitlist;
import com.bookingapp.repository.schedule.ScheduleRepository;
import com.bookingapp.repository.userpackage.UserPackageRepository;
import com.bookingapp.repository.waitlist.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class WaitlistRefundJob {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private WaitlistRepository waitlistRepository;
    @Autowired
    private UserPackageRepository userPackageRepository;

    // Run every hour
    @Scheduled(cron = "0 0 * * * ?") // Every hour on the hour
    public void refundWaitlistUsers() {
        List<Schedule> pastClasses = scheduleRepository.findByEndTimeBefore(LocalDateTime.now());

        for (Schedule schedule : pastClasses) {
            List<Waitlist> waitlist = waitlistRepository.findByScheduleIdOrderByAddedAtAsc(schedule.getId());
            for (Waitlist w : waitlist) {
                if (!w.isRefunded()) {
                    // Refund credits
                    userPackageRepository.findTopByUserIdAndPack_CountryCodeAndExpiryDateAfterOrderByExpiryDateAsc(
                            w.getUser().getId(), schedule.getCountryCode(), LocalDateTime.now()
                    ).ifPresent(pkg -> {
                        pkg.setRemainingCredits(pkg.getRemainingCredits() + schedule.getRequiredCredits());
                        userPackageRepository.save(pkg);
                        w.setRefunded(true);
                        waitlistRepository.save(w);
                    });
                }
            }
        }
    }
}
