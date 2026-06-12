package edu.yachay.backend.academic;

import edu.yachay.backend.academic.domain.models.CalendarEvent;
import edu.yachay.backend.academic.domain.models.Course;
import edu.yachay.backend.academic.domain.models.Enrollment;
import edu.yachay.backend.academic.domain.repositories.CalendarEventRepository;
import edu.yachay.backend.academic.domain.repositories.CourseRepository;
import edu.yachay.backend.academic.domain.repositories.EnrollmentRepository;
import edu.yachay.backend.academic.dto.CalendarEventResponse;
import edu.yachay.backend.academic.dto.CreateCalendarEventRequest;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.models.TeacherProfile;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import edu.yachay.backend.identity.domain.repositories.TeacherProfileRepository;
import edu.yachay.backend.notification.PersistentNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProfileRepository profileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PersistentNotificationService persistentNotificationService;

    public CalendarService(
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

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> adminCalendar() {
        return toResponses(calendarEventRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> adminWeekCalendar() {
        LocalDate start = weekStart(LocalDate.now());
        return filterByRange(calendarEventRepository.findAll(), start, start.plusDays(6));
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> adminMonthCalendar() {
        LocalDate today = LocalDate.now();
        return filterByRange(calendarEventRepository.findAll(), today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth()));
    }

    @Transactional
    public CalendarEventResponse createEvent(CreateCalendarEventRequest request) {
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
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> teacherCalendar(User user) {
        return toResponses(filterTeacherEvents(user));
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> teacherWeekCalendar(User user) {
        LocalDate start = weekStart(LocalDate.now());
        return filterByRange(filterTeacherEvents(user), start, start.plusDays(6));
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> teacherMonthCalendar(User user) {
        LocalDate today = LocalDate.now();
        return filterByRange(filterTeacherEvents(user), today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth()));
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> studentCalendar(User user) {
        return toResponses(filterStudentEvents(user));
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> studentWeekCalendar(User user) {
        LocalDate start = weekStart(LocalDate.now());
        return filterByRange(filterStudentEvents(user), start, start.plusDays(6));
    }

    @Transactional(readOnly = true)
    public List<CalendarEventResponse> studentMonthCalendar(User user) {
        LocalDate today = LocalDate.now();
        return filterByRange(filterStudentEvents(user), today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth()));
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
        String normalized = stripAccents(value);
        if (normalized.isBlank()) {
            return DayOfWeek.MONDAY;
        }

        return switch (normalized) {
            case "MARTES", "MAR" -> DayOfWeek.TUESDAY;
            case "MIERCOLES", "MIE" -> DayOfWeek.WEDNESDAY;
            case "JUEVES", "JUE" -> DayOfWeek.THURSDAY;
            case "VIERNES", "VIE" -> DayOfWeek.FRIDAY;
            case "SABADO", "SAB" -> DayOfWeek.SATURDAY;
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
        return stripAccents(audience).replace(' ', '_');
    }

    private String stripAccents(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
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
            if (event.getCourse().getTeacher() != null && event.getCourse().getTeacher().getProfile() != null) {
                persistentNotificationService.createForUser(event.getCourse().getTeacher().getProfile().getUser(), title, body, "CALENDARIO", "/docente/calendario");
            }

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
