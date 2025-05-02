package com.bookingapp.repository.schedule;

import com.bookingapp.entity.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCountryCode(String countryCode);
    List<Schedule> findByEndTimeBefore(LocalDateTime time);
}
