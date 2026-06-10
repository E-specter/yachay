package edu.yachay.backend.academic;

import edu.yachay.backend.academic.domain.models.*;
import edu.yachay.backend.academic.domain.repositories.*;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.*;
import edu.yachay.backend.notification.PersistentNotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminAcademicController {

    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SchoolRepository schoolRepository;
    private final SchoolSectionRepository schoolSectionRepository;
    private final AcademicTaskRepository academicTaskRepository;
    private final GradeRecordRepository gradeRecordRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PersistentNotificationService persistentNotificationService;

    public AdminAcademicController(
            SubjectRepository subjectRepository,
            CourseRepository courseRepository,
            AcademicYearRepository academicYearRepository,
            TeacherProfileRepository teacherProfileRepository,
            StudentProfileRepository studentProfileRepository,
            SchoolRepository schoolRepository,
            SchoolSectionRepository schoolSectionRepository,
            AcademicTaskRepository academicTaskRepository,
            GradeRecordRepository gradeRecordRepository,
            AnnouncementRepository announcementRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository,
            PersistentNotificationService persistentNotificationService
    ) {
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.academicYearRepository = academicYearRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.schoolRepository = schoolRepository;
        this.schoolSectionRepository = schoolSectionRepository;
        this.academicTaskRepository = academicTaskRepository;
        this.gradeRecordRepository = gradeRecordRepository;
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.persistentNotificationService = persistentNotificationService;
    }

    @GetMapping("/materias")
    @Transactional(readOnly = true)
    public ResponseEntity<List<SubjectResponse>> getSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll().stream()
                .sorted(Comparator.comparing(Subject::getName))
                .map(subject -> new SubjectResponse(subject.getId(), subject.getCode(), subject.getName(), subject.getArea()))
                .toList());
    }

    @GetMapping("/anios-academicos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AcademicYearResponse>> getAcademicYears() {
        return ResponseEntity.ok(academicYearRepository.findAll().stream()
                .sorted(Comparator.comparing(AcademicYear::getYear).reversed())
                .map(year -> new AcademicYearResponse(year.getId(), year.getYear(), year.getIsActive()))
                .toList());
    }

    @GetMapping("/cursos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CourseResponse>> getCourses() {
        return ResponseEntity.ok(courseRepository.findAll().stream()
                .sorted(Comparator.comparing(Course::getId))
                .map(this::toCourse)
                .toList());
    }

    @GetMapping("/cursos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(toCourse(findCourse(id)));
    }

    @PostMapping("/cursos")
    @Transactional
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        if (courseRepository.existsByCode(request.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de curso ya existe.");
        }

        Subject subject = resolveSubject(request);
        AcademicYear academicYear = resolveAcademicYear(request.anioAcademicoId());
        TeacherProfile teacher = findTeacher(request.docenteId());

        Course course = Course.builder()
                .school(defaultSchool())
                .academicYear(academicYear)
                .subject(subject)
                .teacher(teacher)
                .code(request.codigo())
                .name(resolveCourseName(request, subject))
                .gradeLevel(parseGradeLevel(request.grado()))
                .section(request.seccion() != null ? request.seccion() : "A")
                .room(request.aula())
                .maxStudents(request.maximoEstudiantes())
                .isActive(request.activo() == null || request.activo())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(toCourse(courseRepository.save(course)));
    }

    @PatchMapping("/cursos/{id}/estado")
    @Transactional
    public ResponseEntity<CourseResponse> updateCourseStatus(@PathVariable Integer id, @RequestBody UpdateStatusRequest request) {
        Course course = findCourse(id);
        course.setIsActive(!"INACTIVO".equalsIgnoreCase(request.estado()));
        return ResponseEntity.ok(toCourse(courseRepository.save(course)));
    }

    @GetMapping("/secciones")
    @Transactional(readOnly = true)
    public ResponseEntity<List<SectionResponse>> getSections() {
        return ResponseEntity.ok(schoolSectionRepository.findAll().stream()
                .sorted(Comparator.comparing(SchoolSection::getId))
                .map(this::toSection)
                .toList());
    }

    @PostMapping("/secciones")
    @Transactional
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody CreateSectionRequest request) {
        AcademicYear academicYear = resolveAcademicYear(request.anioAcademicoId());
        String name = request.seccion() != null ? request.seccion() : request.nombre();
        if (schoolSectionRepository.existsByAcademicYear_IdAndGradeAndName(academicYear.getId(), request.grado(), name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La seccion ya existe para ese grado y año.");
        }

        SchoolSection section = SchoolSection.builder()
                .school(defaultSchool())
                .academicYear(academicYear)
                .tutorTeacher(request.tutorId() != null ? findTeacher(request.tutorId()) : null)
                .level(request.nivel() != null ? request.nivel() : "Primaria")
                .grade(request.grado())
                .name(name)
                .room(request.aula())
                .capacity(request.capacidad() != null ? request.capacidad() : 30)
                .enrolledCount(0)
                .isActive(request.activo() == null || request.activo())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(toSection(schoolSectionRepository.save(section)));
    }

    @PatchMapping("/secciones/{id}/estado")
    @Transactional
    public ResponseEntity<SectionResponse> updateSectionStatus(@PathVariable Integer id, @RequestBody UpdateStatusRequest request) {
        SchoolSection section = schoolSectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seccion no encontrada."));
        section.setIsActive(!"INACTIVO".equalsIgnoreCase(request.estado()));
        return ResponseEntity.ok(toSection(schoolSectionRepository.save(section)));
    }

    @GetMapping("/tareas")
    @Transactional(readOnly = true)
    public ResponseEntity<List<HomeworkResponse>> getTasks() {
        return ResponseEntity.ok(academicTaskRepository.findAll().stream()
                .sorted(Comparator.comparing(AcademicTask::getId))
                .map(this::toHomework)
                .toList());
    }

    @PostMapping("/tareas")
    @Transactional
    public ResponseEntity<HomeworkResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Course course = findCourse(request.cursoId());
        AcademicTask task = AcademicTask.builder()
                .course(course)
                .teacher(course.getTeacher())
                .title(request.titulo())
                .description(request.descripcion())
                .publishedAt(request.fechaPublicacion() != null ? request.fechaPublicacion() : LocalDateTime.now())
                .dueAt(request.fechaEntrega())
                .maxScore(request.puntajeMaximo() != null ? request.puntajeMaximo() : BigDecimal.valueOf(20))
                .taskType(request.tipo() != null ? request.tipo() : "TAREA")
                .allowLateSubmission(Boolean.TRUE.equals(request.permitirEntregaTardia()))
                .status("PUBLICADA")
                .build();

        AcademicTask savedTask = academicTaskRepository.save(task);
        persistentNotificationService.createForUsers(
                usersForCourse(course),
                "Nueva tarea disponible",
                savedTask.getTitle() + " en " + course.getName(),
                "TAREA",
                "/alumno/tareas"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toHomework(savedTask));
    }

    @PatchMapping("/tareas/{id}/estado")
    @Transactional
    public ResponseEntity<HomeworkResponse> updateTaskStatus(@PathVariable Integer id, @RequestBody UpdateStatusRequest request) {
        AcademicTask task = academicTaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada."));
        task.setStatus(request.estado());
        return ResponseEntity.ok(toHomework(academicTaskRepository.save(task)));
    }

    @GetMapping("/notas")
    @Transactional(readOnly = true)
    public ResponseEntity<List<GradeResponse>> getGrades() {
        return ResponseEntity.ok(gradeRecordRepository.findAll().stream()
                .sorted(Comparator.comparing(GradeRecord::getId))
                .map(this::toGrade)
                .toList());
    }

    @PostMapping("/notas")
    @Transactional
    public ResponseEntity<GradeResponse> createGrade(@Valid @RequestBody CreateGradeRequest request) {
        if (request.nota().compareTo(BigDecimal.ZERO) < 0 || request.nota().compareTo(BigDecimal.valueOf(20)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nota debe estar entre 0 y 20.");
        }

        Course course = findCourse(request.cursoId());
        StudentProfile student = findStudent(request.alumnoId());
        GradeRecord grade = GradeRecord.builder()
                .course(course)
                .student(student)
                .teacher(course.getTeacher())
                .bimester(request.bimestre() != null ? request.bimestre() : "I")
                .score(request.nota())
                .evaluationType(request.tipoEvaluacion() != null ? request.tipoEvaluacion() : "Evaluacion")
                .observation(request.comentario())
                .registeredAt(LocalDateTime.now())
                .status("REGISTRADA")
                .build();

        GradeRecord savedGrade = gradeRecordRepository.save(grade);
        persistentNotificationService.createForUser(
                student.getProfile().getUser(),
                "Nueva nota registrada",
                course.getName() + ": " + savedGrade.getScore(),
                "NOTA",
                "/alumno/notas"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toGrade(savedGrade));
    }

    @PatchMapping("/notas/{id}/estado")
    @Transactional
    public ResponseEntity<GradeResponse> updateGradeStatus(@PathVariable Integer id, @RequestBody UpdateStatusRequest request) {
        GradeRecord grade = gradeRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota no encontrada."));
        grade.setStatus(request.estado());
        return ResponseEntity.ok(toGrade(gradeRecordRepository.save(grade)));
    }

    @GetMapping("/comunicados")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements() {
        return ResponseEntity.ok(announcementRepository.findAll().stream()
                .sorted(Comparator.comparing(Announcement::getId))
                .map(this::toAnnouncement)
                .toList());
    }

    @PostMapping("/comunicados")
    @Transactional
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@Valid @RequestBody CreateAnnouncementRequest request) {
        User author = request.autorId() != null ? userRepository.findById(request.autorId()).orElse(null) : null;
        Announcement announcement = Announcement.builder()
                .school(defaultSchool())
                .course(request.cursoId() != null ? findCourse(request.cursoId()) : null)
                .title(request.titulo())
                .content(request.contenido() != null ? request.contenido() : request.cuerpo())
                .audience(request.destinatario() != null ? request.destinatario() : request.publicoObjetivo())
                .publishedAt(request.fechaPublicacion() != null ? request.fechaPublicacion() : LocalDateTime.now())
                .expiresAt(request.fechaExpiracion())
                .pinned(Boolean.TRUE.equals(request.fijado()))
                .author(author)
                .status("PUBLICADO")
                .build();

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        if (savedAnnouncement.getCourse() != null) {
            persistentNotificationService.createForUsers(
                    usersForCourse(savedAnnouncement.getCourse()),
                    "Nuevo comunicado",
                    savedAnnouncement.getTitle(),
                    "COMUNICADO",
                    "/alumno/comunicados"
            );
        } else {
            persistentNotificationService.createForAudience(
                    savedAnnouncement.getAudience(),
                    "Nuevo comunicado",
                    savedAnnouncement.getTitle(),
                    "COMUNICADO",
                    "/admin/comunicados"
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toAnnouncement(savedAnnouncement));
    }

    @PatchMapping("/comunicados/{id}/estado")
    @Transactional
    public ResponseEntity<AnnouncementResponse> updateAnnouncementStatus(@PathVariable Integer id, @RequestBody UpdateStatusRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunicado no encontrado."));
        announcement.setStatus(request.estado());
        return ResponseEntity.ok(toAnnouncement(announcementRepository.save(announcement)));
    }

    private Course findCourse(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado."));
    }

    private TeacherProfile findTeacher(Integer id) {
        return teacherProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado."));
    }

    private StudentProfile findStudent(Integer id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado."));
    }

    private School defaultSchool() {
        return schoolRepository.findByCode("MGP")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Colegio MGP no existe."));
    }

    private AcademicYear resolveAcademicYear(Integer id) {
        if (id != null) {
            return academicYearRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Año academico no encontrado."));
        }

        return academicYearRepository.findByYear(2026)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Año academico 2026 no existe."));
    }

    private Subject resolveSubject(CreateCourseRequest request) {
        if (request.materiaId() != null) {
            return subjectRepository.findById(request.materiaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Materia no encontrada."));
        }

        String subjectName = request.materia() != null && !request.materia().isBlank() ? request.materia() : request.area();
        if (subjectName == null || subjectName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Materia o area requerida.");
        }
        String subjectCodeBase = subjectName.toUpperCase().replaceAll("[^A-Z0-9]", "");
        String subjectCode = (subjectCodeBase.isBlank() ? "MAT" : subjectCodeBase.substring(0, Math.min(8, subjectCodeBase.length())))
                + System.currentTimeMillis() % 1000;
        return subjectRepository.findByName(subjectName)
                .orElseGet(() -> subjectRepository.save(Subject.builder()
                        .code(subjectCode)
                        .name(subjectName)
                        .area(subjectName)
                        .isActive(true)
                        .build()));
    }

    private String resolveCourseName(CreateCourseRequest request, Subject subject) {
        if (request.nombre() != null && !request.nombre().isBlank()) return request.nombre();
        return subject.getName() + " " + request.grado() + " " + (request.seccion() != null ? request.seccion() : "A");
    }

    private Short parseGradeLevel(String grade) {
        if (grade == null || grade.isBlank()) return 3;
        String digits = grade.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return 3;
        return Short.valueOf(digits.substring(0, 1));
    }

    private String fullName(Profile profile) {
        return profile != null ? profile.getFullName() : "";
    }

    private List<User> usersForCourse(Course course) {
        return enrollmentRepository.findByCourse_Id(course.getId()).stream()
                .map(Enrollment::getStudent)
                .map(StudentProfile::getProfile)
                .map(Profile::getUser)
                .toList();
    }

    private CourseResponse toCourse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getCode(),
                "Primaria",
                course.getGradeLevel() + " Primaria",
                course.getSubject() != null ? course.getSubject().getArea() : "",
                course.getTeacher() != null ? fullName(course.getTeacher().getProfile()) : "Sin asignar",
                Boolean.TRUE.equals(course.getIsActive()) ? "ACTIVO" : "INACTIVO"
        );
    }

    private SectionResponse toSection(SchoolSection section) {
        return new SectionResponse(
                section.getId(),
                section.getLevel(),
                section.getGrade(),
                section.getName(),
                section.getTutorTeacher() != null ? fullName(section.getTutorTeacher().getProfile()) : "Sin tutor",
                section.getCapacity(),
                section.getEnrolledCount(),
                Boolean.TRUE.equals(section.getIsActive()) ? "ACTIVO" : "INACTIVO"
        );
    }

    private HomeworkResponse toHomework(AcademicTask task) {
        Course course = task.getCourse();
        return new HomeworkResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                course != null ? course.getName() : "",
                task.getTeacher() != null ? fullName(task.getTeacher().getProfile()) : "",
                "Primaria",
                course != null ? course.getGradeLevel() + " Primaria" : "",
                course != null ? course.getSection() : "A",
                task.getPublishedAt() != null ? task.getPublishedAt().toLocalDate().toString() : "",
                task.getDueAt() != null ? task.getDueAt().toLocalDate().toString() : "",
                task.getStatus()
        );
    }

    private GradeResponse toGrade(GradeRecord grade) {
        return new GradeResponse(
                grade.getId(),
                grade.getStudent() != null ? fullName(grade.getStudent().getProfile()) : "",
                grade.getCourse() != null ? grade.getCourse().getName() : "",
                grade.getTeacher() != null ? fullName(grade.getTeacher().getProfile()) : "",
                grade.getBimester(),
                grade.getScore(),
                grade.getRegisteredAt() != null ? grade.getRegisteredAt().toLocalDate().toString() : "",
                grade.getStatus()
        );
    }

    private AnnouncementResponse toAnnouncement(Announcement announcement) {
        Course course = announcement.getCourse();
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getAudience(),
                course != null ? "Primaria" : null,
                course != null ? course.getGradeLevel() + " Primaria" : null,
                course != null ? course.getSection() : null,
                announcement.getPublishedAt() != null ? announcement.getPublishedAt().toLocalDate().toString() : "",
                announcement.getStatus()
        );
    }

    public record SubjectResponse(Integer id, String codigo, String nombre, String area) {
    }

    public record AcademicYearResponse(Integer id, Integer anio, Boolean activo) {
    }

    public record CourseResponse(Integer id, String nombre, String codigo, String nivel, String grado, String area, String docenteAsignado, String estado) {
    }

    public record SectionResponse(Integer id, String nivel, String grado, String seccion, String tutor, Integer capacidad, Integer matriculados, String estado) {
    }

    public record HomeworkResponse(Integer id, String titulo, String descripcion, String curso, String docente, String nivel, String grado, String seccion, String fechaPublicacion, String fechaEntrega, String estado) {
    }

    public record GradeResponse(Integer id, String alumno, String curso, String docente, String bimestre, BigDecimal nota, String fechaRegistro, String estado) {
    }

    public record AnnouncementResponse(Integer id, String titulo, String contenido, String destinatario, String nivel, String grado, String seccion, String fechaPublicacion, String estado) {
    }

    public record CreateCourseRequest(String nombre, @NotBlank String codigo, String nivel, @NotBlank String grado, String seccion, String area, String materia, Integer materiaId, @NotNull Integer docenteId, Integer anioAcademicoId, String aula, Integer maximoEstudiantes, Boolean activo) {
    }

    public record CreateSectionRequest(String nivel, @NotBlank String grado, String seccion, String nombre, Integer tutorId, String aula, Integer anioAcademicoId, Integer capacidad, Boolean activo) {
    }

    public record CreateTaskRequest(@NotNull Integer cursoId, @NotBlank String titulo, String descripcion, LocalDateTime fechaPublicacion, @NotNull LocalDateTime fechaEntrega, BigDecimal puntajeMaximo, String tipo, Boolean permitirEntregaTardia) {
    }

    public record CreateGradeRequest(@NotNull Integer alumnoId, @NotNull Integer cursoId, String bimestre, @NotNull BigDecimal nota, String tipoEvaluacion, String comentario) {
    }

    public record CreateAnnouncementRequest(@NotBlank String titulo, String contenido, String cuerpo, String destinatario, String publicoObjetivo, Boolean fijado, LocalDateTime fechaPublicacion, LocalDateTime fechaExpiracion, Long autorId, Integer cursoId) {
    }

    public record UpdateStatusRequest(@NotBlank String estado) {
    }
}
