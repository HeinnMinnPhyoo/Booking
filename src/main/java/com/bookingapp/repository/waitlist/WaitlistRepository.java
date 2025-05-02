package com.bookingapp.repository.waitlist;

import com.bookingapp.entity.waitlist.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findByScheduleIdOrderByAddedAtAsc(Long scheduleId);
}
