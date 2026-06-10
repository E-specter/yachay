package edu.yachay.backend.academic;

import edu.yachay.backend.academic.domain.models.*;
import edu.yachay.backend.academic.domain.repositories.*;
import edu.yachay.backend.academic.dto.*;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.*;
import edu.yachay.backend.notification.PersistentNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.TextStyle;
import java.time.*;
import java.util.*;

@RestController
public class CalendarController {

    private final CalendarEventRepository calendarEventRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProfileRepository profileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PersistentNotificationService persistentNotificationService;

    public CalendarController(
            CalendarEventRepository calendarEventRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            ProfileRepository profileRepository,
            TeacherProfileRepository teacherProfileRepository,
            StudentProfileRepository studentProfileRepository,
            PersistentNotificationService persistentNotificationService
    ) {
        this.calendarEventRepository = calendarEventRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.profileRepository = profileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.persistentNotificationService = persistentNotificationService;
    }

    @GetMapping("/admin/calendario")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> adminCalendar() {
        return ResponseEntity.ok(toResponses(calendarEventRepository.findAll()));
    }

    @GetMapping("/admin/calendario/semana")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> adminWeekCalendar() {
        return ResponseEntity.ok(filterByRange(calendarEventRepository.findAll(), weekStart(LocalDate.now()), weekStart(LocalDate.now()).plusDays(6)));
    }

    @GetMapping("/admin/calendario/mes")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> adminMonthCalendar() {
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(filterByRange(calendarEventRepository.findAll(), today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth())));
    }

    @PostMapping("/admin/calendario")
    @Transactional
    public ResponseEntity<CalendarEventResponse> createEvent(@Valid @RequestBody CreateCalendarEventRequest request) {
        if (!request.fechaFin().isAfter(request.fechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha fin debe ser posterior a la fecha inicio.");
        }

        Course course = request.cursoId() != null
                ? courseRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso no encontrado."))
                : null;

        CalendarEvent event = CalendarEvent.builder()
                .course(course)
                .title(request.titulo())
                .description(request.descripcion())
                .eventType(request.tipo())
                .audience(request.publicoObjetivo() != null ? request.publicoObjetivo() : "TODOS")
                .eventDate(request.fechaInicio().toLocalDate())
                .dayOfWeek(spanishDay(request.fechaInicio().getDayOfWeek()))
                .startTime(request.fechaInicio().toLocalTime())
                .endTime(request.fechaFin().toLocalTime())
                .build();

        CalendarEvent saved = calendarEventRepository.save(event);
        notifyCalendarEvent(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/docente/calendario")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> teacherCalendar(Authentication authentication) {
        return ResponseEntity.ok(toResponses(filterTeacherEvents(currentUser(authentication))));
    }

    @GetMapping("/docente/calendario/semana")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> teacherWeekCalendar(Authentication authentication) {
        LocalDate start = weekStart(LocalDate.now());
        return ResponseEntity.ok(filterByRange(filterTeacherEvents(currentUser(authentication)), start, start.plusDays(6)));
    }

    @GetMapping("/docente/calendario/mes")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> teacherMonthCalendar(Authentication authentication) {
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(filterByRange(filterTeacherEvents(currentUser(authentication)), today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth())));
    }

    @GetMapping("/alumno/calendario")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> studentCalendar(Authentication authentication) {
        return ResponseEntity.ok(toResponses(filterStudentEvents(currentUser(authentication))));
    }

    @GetMapping("/alumno/calendario/semana")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> studentWeekCalendar(Authentication authentication) {
        LocalDate start = weekStart(LocalDate.now());
        return ResponseEntity.ok(filterByRange(filterStudentEvents(currentUser(authentication)), start, start.plusDays(6)));
    }

    @GetMapping("/alumno/calendario/mes")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CalendarEventResponse>> studentMonthCalendar(Authentication authentication) {
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(filterByRange(filterStudentEvents(currentUser(authentication)), today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth())));
    }

    private List<CalendarEvent> filterTeacherEvents(User user) {
        TeacherProfile teacher = teacherForUser(user);
        return calendarEventRepository.findAll().stream()
                .filter(event -> isGeneralFor("DOCENTE", event)
                        || (event.getCourse() != null
                        && event.getCourse().getTeacher() != null
                        && event.getCourse().getTeacher().getId().equals(teacher.getId())))
                .toList();
    }

    private List<CalendarEvent> filterStudentEvents(User user) {
        StudentProfile student = studentForUser(user);
        Set<Integer> enrolledCourseIds = new HashSet<>();
        enrollmentRepository.findByStudent_Id(student.getId()).stream()
                .map(Enrollment::getCourse)
                .filter(course -> course != null && course.getId() != null)
                .map(Course::getId)
                .forEach(enrolledCourseIds::add);

        return calendarEventRepository.findAll().stream()
                .filter(event -> isGeneralFor("ALUMNO", event)
                        || (event.getStudent() != null && event.getStudent().getId().equals(student.getId()))
                        || (event.getCourse() != null && enrolledCourseIds.contains(event.getCourse().getId())))
                .toList();
    }

    private boolean isGeneralFor(String role, CalendarEvent event) {
        String audience = normalizeAudience(event.getAudience());
        return audience.equals("TODOS")
                || audience.equals(role)
                || (role.equals("ALUMNO") && audience.equals("ALUMNOS_APODERADOS"));
    }

    private List<CalendarEventResponse> filterByRange(List<CalendarEvent> events, LocalDate start, LocalDate end) {
        return toResponses(events).stream()
                .filter(event -> {
                    LocalDate date = event.startDateTime().toLocalDate();
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .toList();
    }

    private List<CalendarEventResponse> toResponses(List<CalendarEvent> events) {
        return events.stream()
                .sorted(Comparator
                        .comparing((CalendarEvent event) -> resolvedStart(event).toLocalDate())
                        .thenComparing(CalendarEvent::getStartTime)
                        .thenComparing(CalendarEvent::getId))
                .map(this::toResponse)
                .toList();
    }

    private CalendarEventResponse toResponse(CalendarEvent event) {
        Course course = event.getCourse();
        String sectionName = course != null
                ? course.getGradeLevel() + " Primaria " + course.getSection()
                : null;

        return new CalendarEventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                resolvedStart(event),
                resolvedEnd(event),
                event.getEventType(),
                course != null ? course.getName() : null,
                sectionName,
                event.getAudience()
        );
    }

    private LocalDateTime resolvedStart(CalendarEvent event) {
        return LocalDateTime.of(resolvedDate(event), event.getStartTime());
    }

    private LocalDateTime resolvedEnd(CalendarEvent event) {
        return LocalDateTime.of(resolvedDate(event), event.getEndTime());
    }

    private LocalDate resolvedDate(CalendarEvent event) {
        if (event.getEventDate() != null) {
            return event.getEventDate();
        }

        DayOfWeek dayOfWeek = parseDay(event.getDayOfWeek());
        LocalDate start = weekStart(LocalDate.now());
        return start.plusDays(dayOfWeek.getValue() - 1L);
    }

    private LocalDate weekStart(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1L);
    }

    private DayOfWeek parseDay(String value) {
        if (value == null || value.isBlank()) {
            return DayOfWeek.MONDAY;
        }

        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "MARTES", "MAR" -> DayOfWeek.TUESDAY;
            case "MIERCOLES", "MIÉRCOLES", "MIE" -> DayOfWeek.WEDNESDAY;
            case "JUEVES", "JUE" -> DayOfWeek.THURSDAY;
            case "VIERNES", "VIE" -> DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO", "SAB" -> DayOfWeek.SATURDAY;
            case "DOMINGO", "DOM" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };
    }

    private String spanishDay(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("es", "PE")).toUpperCase(Locale.ROOT);
    }

    private String normalizeAudience(String audience) {
        if (audience == null || audience.isBlank()) {
            return "TODOS";
        }
        return audience.trim().toUpperCase(Locale.ROOT);
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.");
        }
        return user;
    }

    private TeacherProfile teacherForUser(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado."));
        return teacherProfileRepository.findByProfileId(profile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil docente no encontrado."));
    }

    private StudentProfile studentForUser(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado."));
        return studentProfileRepository.findByProfileId(profile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil alumno no encontrado."));
    }

    private void notifyCalendarEvent(CalendarEvent event) {
        String title = "Evento academico programado";
        String body = event.getTitle() + " - " + resolvedStart(event).toLocalDate();

        if (event.getCourse() != null) {
            persistentNotificationService.createForUser(event.getCourse().getTeacher().getProfile().getUser(), title, body, "CALENDARIO", "/docente/calendario");
            List<User> students = enrollmentRepository.findByCourse_Id(event.getCourse().getId()).stream()
                    .map(Enrollment::getStudent)
                    .map(StudentProfile::getProfile)
                    .map(Profile::getUser)
                    .toList();
            persistentNotificationService.createForUsers(students, title, body, "CALENDARIO", "/alumno/calendario");
            return;
        }

        persistentNotificationService.createForAudience(event.getAudience(), title, body, "CALENDARIO", "/admin/calendario");
    }
}
