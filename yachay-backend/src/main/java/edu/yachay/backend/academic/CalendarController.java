package edu.yachay.backend.academic;

import edu.yachay.backend.academic.dto.CalendarEventResponse;
import edu.yachay.backend.academic.dto.CreateCalendarEventRequest;
import edu.yachay.backend.identity.domain.models.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/admin/calendario")
    public ResponseEntity<List<CalendarEventResponse>> adminCalendar() {
        return ResponseEntity.ok(calendarService.adminCalendar());
    }

    @GetMapping("/admin/calendario/semana")
    public ResponseEntity<List<CalendarEventResponse>> adminWeekCalendar() {
        return ResponseEntity.ok(calendarService.adminWeekCalendar());
    }

    @GetMapping("/admin/calendario/mes")
    public ResponseEntity<List<CalendarEventResponse>> adminMonthCalendar() {
        return ResponseEntity.ok(calendarService.adminMonthCalendar());
    }

    @PostMapping("/admin/calendario")
    public ResponseEntity<CalendarEventResponse> createEvent(@Valid @RequestBody CreateCalendarEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarService.createEvent(request));
    }

    @GetMapping("/docente/calendario")
    public ResponseEntity<List<CalendarEventResponse>> teacherCalendar(Authentication authentication) {
        return ResponseEntity.ok(calendarService.teacherCalendar(currentUser(authentication)));
    }

    @GetMapping("/docente/calendario/semana")
    public ResponseEntity<List<CalendarEventResponse>> teacherWeekCalendar(Authentication authentication) {
        return ResponseEntity.ok(calendarService.teacherWeekCalendar(currentUser(authentication)));
    }

    @GetMapping("/docente/calendario/mes")
    public ResponseEntity<List<CalendarEventResponse>> teacherMonthCalendar(Authentication authentication) {
        return ResponseEntity.ok(calendarService.teacherMonthCalendar(currentUser(authentication)));
    }

    @GetMapping("/alumno/calendario")
    public ResponseEntity<List<CalendarEventResponse>> studentCalendar(Authentication authentication) {
        return ResponseEntity.ok(calendarService.studentCalendar(currentUser(authentication)));
    }

    @GetMapping("/alumno/calendario/semana")
    public ResponseEntity<List<CalendarEventResponse>> studentWeekCalendar(Authentication authentication) {
        return ResponseEntity.ok(calendarService.studentWeekCalendar(currentUser(authentication)));
    }

    @GetMapping("/alumno/calendario/mes")
    public ResponseEntity<List<CalendarEventResponse>> studentMonthCalendar(Authentication authentication) {
        return ResponseEntity.ok(calendarService.studentMonthCalendar(currentUser(authentication)));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.");
        }
        return user;
    }
}
