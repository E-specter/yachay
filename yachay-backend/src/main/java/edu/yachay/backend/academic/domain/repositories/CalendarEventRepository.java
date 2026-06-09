package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    boolean existsByTitleAndDayOfWeekAndStartTime(String title, String dayOfWeek, LocalTime startTime);
}
