package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    boolean existsByTitleAndDayOfWeekAndStartTime(String title, String dayOfWeek, LocalTime startTime);

    List<CalendarEvent> findByEventDateBetweenOrderByEventDateAscStartTimeAsc(LocalDate startDate, LocalDate endDate);
}
