package edu.yachay.backend.portal;

import edu.yachay.backend.academic.domain.models.*;
import edu.yachay.backend.academic.domain.repositories.*;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.models.TeacherProfile;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import edu.yachay.backend.identity.domain.repositories.TeacherProfileRepository;
import edu.yachay.backend.portal.PortalDtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PortalService {
    private final TeacherProfileRepository teacherRepository;
    private final StudentProfileRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicTaskRepository taskRepository;
    private final GradeRecordRepository gradeRepository;
    private final AnnouncementRepository announcementRepository;
    private final HomeworkSubmissionRepository submissionRepository;
    private final AnnouncementReadRepository announcementReadRepository;

    public PortalService(
            TeacherProfileRepository teacherRepository,
            StudentProfileRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AcademicTaskRepository taskRepository,
            GradeRecordRepository gradeRepository,
            AnnouncementRepository announcementRepository,
            HomeworkSubmissionRepository submissionRepository,
            AnnouncementReadRepository announcementReadRepository
    ) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.taskRepository = taskRepository;
        this.gradeRepository = gradeRepository;
        this.announcementRepository = announcementRepository;
        this.submissionRepository = submissionRepository;
        this.announcementReadRepository = announcementReadRepository;
    }

    public List<TeacherCourse> teacherCourses(User user) {
        TeacherProfile teacher = teacher(user);
        return courseRepository.findByTeacher_IdOrderByNameAsc(teacher.getId()).stream()
                .map(this::toTeacherCourse)
                .toList();
    }

    public List<TeacherStudent> teacherStudents(User user) {
        TeacherProfile teacher = teacher(user);
        return enrollmentRepository.findByCourse_Teacher_Id(teacher.getId()).stream()
                .map(enrollment -> toTeacherStudent(enrollment, average(enrollment.getStudent(), enrollment.getCourse())))
                .toList();
    }

    public List<TeacherTask> teacherTasks(User user) {
        TeacherProfile teacher = teacher(user);
        return taskRepository.findByTeacher_IdOrderByDueAtDesc(teacher.getId()).stream()
                .map(this::toTeacherTask)
                .toList();
    }

    public List<TeacherGrade> teacherGrades(User user) {
        return gradeRepository.findByTeacher_IdOrderByRegisteredAtDesc(teacher(user).getId()).stream()
                .map(this::toTeacherGrade)
                .toList();
    }

    public List<TeacherAnnouncement> teacherAnnouncements(User user) {
        teacher(user);
        return announcementRepository.findByAuthor_IdOrderByPublishedAtDesc(user.getId()).stream()
                .map(this::toTeacherAnnouncement)
                .toList();
    }

    public TeacherProfileDto teacherProfile(User user) {
        TeacherProfile teacher = teacher(user);
        Profile profile = teacher.getProfile();
        return new TeacherProfileDto(
                nvl(teacher.getEmployeeId()),
                nvl(profile.getFirstName()),
                nvl(profile.getLastName()),
                document(profile),
                nvl(user.getEmail()),
                nvl(teacher.getSpecialization()),
                nvl(user.getPhone()),
                date(teacher.getHireDate()),
                teacher.getSchool() != null ? nvl(teacher.getSchool().getName()) : ""
        );
    }

    public TeacherDashboard teacherDashboard(User user) {
        TeacherProfile teacher = teacher(user);
        List<Course> courses = courseRepository.findByTeacher_IdOrderByNameAsc(teacher.getId());
        List<AcademicTask> tasks = taskRepository.findByTeacher_IdOrderByDueAtDesc(teacher.getId());
        List<GradeRecord> grades = gradeRepository.findByTeacher_IdOrderByRegisteredAtDesc(teacher.getId());
        List<Announcement> announcements = announcementRepository.findByAuthor_IdOrderByPublishedAtDesc(user.getId());
        long studentCount = enrollmentRepository.findByCourse_Teacher_Id(teacher.getId()).stream()
                .map(enrollment -> enrollment.getStudent().getId())
                .distinct()
                .count();

        List<TeacherReviewTask> reviewTasks = tasks.stream()
                .filter(task -> !"CERRADA".equalsIgnoreCase(task.getStatus()))
                .sorted(Comparator.comparing(AcademicTask::getDueAt))
                .limit(5)
                .map(task -> new TeacherReviewTask(
                        task.getId(), task.getTitle(), task.getCourse().getName(), aula(task.getCourse()),
                        date(task.getDueAt()), pending(task)))
                .toList();

        return new TeacherDashboard(
                List.of(
                        new TeacherMetric("Cursos asignados", courses.size()),
                        new TeacherMetric("Alumnos a cargo", studentCount),
                        new TeacherMetric("Tareas publicadas", tasks.size()),
                        new TeacherMetric("Notas registradas", grades.size()),
                        new TeacherMetric("Comunicados publicados", announcements.stream()
                                .filter(item -> "PUBLICADO".equalsIgnoreCase(item.getStatus())).count())
                ),
                reviewTasks,
                announcements.stream().limit(5)
                        .map(item -> new TeacherDashboardAnnouncement(item.getId(), item.getTitle(),
                                date(item.getPublishedAt()), item.getStatus()))
                        .toList(),
                courses.stream().map(course -> new TeacherAssignedCourse(
                        course.getId(), course.getCode(), course.getName(), aula(course),
                        enrollmentRepository.findByCourse_Id(course.getId()).size()))
                        .toList()
        );
    }

    @Transactional
    public TeacherTask createTeacherTask(User user, TaskRequest request) {
        TeacherProfile teacher = teacher(user);
        Course course = teacherCourse(teacher, request.cursoId());
        if (taskRepository.existsByCourse_IdAndTitle(course.getId(), request.titulo().trim())) {
            throw conflict("Ya existe una tarea con ese titulo en el curso.");
        }
        AcademicTask task = AcademicTask.builder()
                .course(course)
                .teacher(teacher)
                .title(request.titulo().trim())
                .description(nvl(request.descripcion()).trim())
                .publishedAt(request.fechaPublicacion() == null ? LocalDateTime.now() : request.fechaPublicacion())
                .dueAt(request.fechaEntrega())
                .maxScore(request.puntajeMaximo() == null ? BigDecimal.valueOf(20) : request.puntajeMaximo())
                .taskType(blankDefault(request.tipo(), "TAREA"))
                .allowLateSubmission(Boolean.TRUE.equals(request.permitirEntregaTardia()))
                .status(taskStatus(request.estado(), "BORRADOR"))
                .build();
        return toTeacherTask(taskRepository.save(task));
    }

    @Transactional
    public TeacherTask updateTeacherTask(User user, Integer id, TaskRequest request) {
        TeacherProfile teacher = teacher(user);
        AcademicTask task = teacherTask(teacher, id);
        Course course = teacherCourse(teacher, request.cursoId());
        task.setCourse(course);
        task.setTitle(request.titulo().trim());
        task.setDescription(nvl(request.descripcion()).trim());
        task.setPublishedAt(request.fechaPublicacion() == null ? task.getPublishedAt() : request.fechaPublicacion());
        task.setDueAt(request.fechaEntrega());
        task.setMaxScore(request.puntajeMaximo() == null ? task.getMaxScore() : request.puntajeMaximo());
        task.setTaskType(blankDefault(request.tipo(), task.getTaskType()));
        task.setAllowLateSubmission(Boolean.TRUE.equals(request.permitirEntregaTardia()));
        task.setStatus(taskStatus(request.estado(), task.getStatus()));
        return toTeacherTask(taskRepository.save(task));
    }

    @Transactional
    public TeacherTask updateTeacherTaskStatus(User user, Integer id, StatusRequest request) {
        AcademicTask task = teacherTask(teacher(user), id);
        task.setStatus(taskStatus(request.estado(), task.getStatus()));
        return toTeacherTask(taskRepository.save(task));
    }

    public List<SubmissionResponse> taskSubmissions(User user, Integer taskId) {
        AcademicTask task = teacherTask(teacher(user), taskId);
        return submissionRepository.findByTask_Id(task.getId()).stream().map(this::toSubmission).toList();
    }

    @Transactional
    public TeacherGrade createTeacherGrade(User user, GradeRequest request) {
        TeacherProfile teacher = teacher(user);
        Course course = teacherCourse(teacher, request.cursoId());
        StudentProfile student = enrolledStudent(course, request.alumnoId());
        if (gradeRepository.existsByCourse_IdAndStudent_IdAndBimester(course.getId(), student.getId(), request.bimestre())) {
            throw conflict("Ya existe una nota para ese alumno, curso y bimestre.");
        }
        GradeRecord grade = GradeRecord.builder()
                .course(course).student(student).teacher(teacher)
                .bimester(request.bimestre().trim().toUpperCase(Locale.ROOT))
                .score(request.nota()).evaluationType(nvl(request.tipoEvaluacion()))
                .observation(nvl(request.observacion())).registeredAt(LocalDateTime.now())
                .status(gradeStatus(request.estado(), "REGISTRADA"))
                .build();
        return toTeacherGrade(gradeRepository.save(grade));
    }

    @Transactional
    public TeacherGrade updateTeacherGrade(User user, Integer id, GradeRequest request) {
        TeacherProfile teacher = teacher(user);
        GradeRecord grade = teacherGrade(teacher, id);
        Course course = teacherCourse(teacher, request.cursoId());
        StudentProfile student = enrolledStudent(course, request.alumnoId());
        grade.setCourse(course);
        grade.setStudent(student);
        grade.setBimester(request.bimestre().trim().toUpperCase(Locale.ROOT));
        grade.setScore(request.nota());
        grade.setEvaluationType(nvl(request.tipoEvaluacion()));
        grade.setObservation(nvl(request.observacion()));
        grade.setStatus(gradeStatus(request.estado(), grade.getStatus()));
        return toTeacherGrade(gradeRepository.save(grade));
    }

    @Transactional
    public TeacherGrade updateTeacherGradeStatus(User user, Integer id, StatusRequest request) {
        GradeRecord grade = teacherGrade(teacher(user), id);
        grade.setStatus(gradeStatus(request.estado(), grade.getStatus()));
        return toTeacherGrade(gradeRepository.save(grade));
    }

    @Transactional
    public TeacherAnnouncement createTeacherAnnouncement(User user, AnnouncementRequest request) {
        TeacherProfile teacher = teacher(user);
        Course course = request.cursoId() == null ? null : teacherCourse(teacher, request.cursoId());
        Announcement announcement = Announcement.builder()
                .school(teacher.getSchool()).course(course).title(request.titulo().trim())
                .content(request.contenido().trim()).audience(blankDefault(request.destinatario(), "ALUMNOS"))
                .publishedAt(request.fechaPublicacion() == null ? LocalDateTime.now() : request.fechaPublicacion())
                .expiresAt(request.fechaExpiracion()).pinned(Boolean.TRUE.equals(request.fijado()))
                .author(user).status(announcementStatus(request.estado(), "BORRADOR"))
                .build();
        return toTeacherAnnouncement(announcementRepository.save(announcement));
    }

    @Transactional
    public TeacherAnnouncement updateTeacherAnnouncement(User user, Integer id, AnnouncementRequest request) {
        TeacherProfile teacher = teacher(user);
        Announcement announcement = teacherAnnouncement(user, id);
        announcement.setCourse(request.cursoId() == null ? null : teacherCourse(teacher, request.cursoId()));
        announcement.setTitle(request.titulo().trim());
        announcement.setContent(request.contenido().trim());
        announcement.setAudience(blankDefault(request.destinatario(), announcement.getAudience()));
        announcement.setPublishedAt(request.fechaPublicacion() == null ? announcement.getPublishedAt() : request.fechaPublicacion());
        announcement.setExpiresAt(request.fechaExpiracion());
        announcement.setPinned(Boolean.TRUE.equals(request.fijado()));
        announcement.setStatus(announcementStatus(request.estado(), announcement.getStatus()));
        return toTeacherAnnouncement(announcementRepository.save(announcement));
    }

    @Transactional
    public TeacherAnnouncement updateTeacherAnnouncementStatus(User user, Integer id, StatusRequest request) {
        Announcement announcement = teacherAnnouncement(user, id);
        announcement.setStatus(announcementStatus(request.estado(), announcement.getStatus()));
        return toTeacherAnnouncement(announcementRepository.save(announcement));
    }

    public List<StudentCourse> studentCourses(User user) {
        StudentProfile student = student(user);
        return enrollmentRepository.findByStudent_Id(student.getId()).stream()
                .map(enrollment -> toStudentCourse(enrollment.getCourse(), average(student, enrollment.getCourse()), enrollment.getStatus()))
                .toList();
    }

    public StudentCourseDetail studentCourseDetail(User user, Integer courseId) {
        StudentProfile student = student(user);
        Course course = studentCourse(student, courseId);
        List<AcademicTask> tasks = taskRepository.findByCourse_IdInOrderByDueAtAsc(List.of(courseId));
        List<GradeRecord> grades = gradeRepository.findByStudent_IdOrderByRegisteredAtDesc(student.getId()).stream()
                .filter(grade -> courseId.equals(grade.getCourse().getId())).toList();
        List<Announcement> announcements = studentAnnouncementsFor(student).stream()
                .filter(item -> item.getCourse() != null && courseId.equals(item.getCourse().getId())).toList();
        long total = tasks.size();
        long delivered = tasks.stream().filter(task -> submissionRepository
                .findByTask_IdAndStudent_Id(task.getId(), student.getId()).isPresent()).count();
        String progress = total == 0 ? "0%" : Math.round(delivered * 100.0 / total) + "%";
        return new StudentCourseDetail(
                course.getId(), course.getName(), teacherName(course), aula(course), progress,
                tasks.stream().filter(task -> task.getDueAt().isAfter(LocalDateTime.now())).findFirst()
                        .map(AcademicTask::getTitle).orElse("Sin actividades pendientes"),
                "Curso de " + course.getSubject().getName() + ".",
                List.of(),
                tasks.stream().limit(5).map(AcademicTask::getTitle).toList(),
                grades.stream().limit(5).map(grade -> grade.getScore() + " en " + grade.getBimester()).toList(),
                announcements.stream().limit(5).map(Announcement::getTitle).toList()
        );
    }

    public List<StudentTask> studentTasks(User user) {
        StudentProfile student = student(user);
        List<Integer> courseIds = studentCourseIds(student);
        if (courseIds.isEmpty()) return List.of();
        return taskRepository.findByCourse_IdInOrderByDueAtAsc(courseIds).stream()
                .map(task -> toStudentTask(task, submissionRepository
                        .findByTask_IdAndStudent_Id(task.getId(), student.getId()).orElse(null)))
                .toList();
    }

    public StudentTaskDetail studentTaskDetail(User user, Integer taskId) {
        StudentProfile student = student(user);
        AcademicTask task = studentTask(student, taskId);
        HomeworkSubmission submission = submissionRepository
                .findByTask_IdAndStudent_Id(taskId, student.getId()).orElse(null);
        return new StudentTaskDetail(
                task.getId(), task.getTitle(), task.getCourse().getName(), teacherName(task.getCourse()),
                date(task.getPublishedAt()), date(task.getDueAt()), deliveryStatus(task, submission),
                nvl(task.getDescription()), nvl(task.getDescription()), "", submission == null ? "" : submission.getContent(),
                submission == null ? "" : date(submission.getSubmittedAt())
        );
    }

    @Transactional
    public SubmissionResponse submitHomework(User user, Integer taskId, SubmissionRequest request) {
        StudentProfile student = student(user);
        AcademicTask task = studentTask(student, taskId);
        if (task.getDueAt().isBefore(LocalDateTime.now()) && !Boolean.TRUE.equals(task.getAllowLateSubmission())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La fecha limite vencio y la tarea no admite entregas tardias.");
        }
        HomeworkSubmission submission = submissionRepository
                .findByTask_IdAndStudent_Id(taskId, student.getId())
                .orElseGet(() -> HomeworkSubmission.builder().task(task).student(student).build());
        submission.setContent(request.contenido().trim());
        submission.setAttachmentUrl(nvl(request.archivoUrl()).trim());
        submission.setStatus("ENTREGADA");
        submission.setSubmittedAt(LocalDateTime.now());
        return toSubmission(submissionRepository.save(submission));
    }

    public List<StudentGrade> studentGrades(User user) {
        return gradeRepository.findByStudent_IdOrderByRegisteredAtDesc(student(user).getId()).stream()
                .filter(grade -> !"ANULADA".equalsIgnoreCase(grade.getStatus()))
                .map(grade -> new StudentGrade(grade.getId(), grade.getCourse().getName(),
                        teacherName(grade.getCourse()), grade.getBimester(), grade.getScore(),
                        nvl(grade.getObservation()), date(grade.getRegisteredAt())))
                .toList();
    }

    public List<StudentAnnouncement> studentAnnouncements(User user) {
        StudentProfile student = student(user);
        Set<Integer> readIds = announcementReadRepository.findByStudent_Id(student.getId()).stream()
                .map(item -> item.getAnnouncement().getId()).collect(Collectors.toSet());
        return studentAnnouncementsFor(student).stream()
                .map(item -> new StudentAnnouncement(item.getId(), item.getTitle(), item.getContent(),
                        item.getAuthor() == null ? "Administracion" : displayName(item.getAuthor()),
                        date(item.getPublishedAt()), readIds.contains(item.getId())))
                .toList();
    }

    public StudentAnnouncement studentAnnouncement(User user, Integer announcementId) {
        return studentAnnouncements(user).stream().filter(item -> item.id().equals(announcementId)).findFirst()
                .orElseThrow(() -> notFound("Comunicado no encontrado."));
    }

    @Transactional
    public StudentAnnouncement markAnnouncementRead(User user, Integer announcementId) {
        StudentProfile student = student(user);
        Announcement announcement = studentAnnouncementsFor(student).stream()
                .filter(item -> item.getId().equals(announcementId)).findFirst()
                .orElseThrow(() -> notFound("Comunicado no encontrado."));
        announcementReadRepository.findByAnnouncement_IdAndStudent_Id(announcementId, student.getId())
                .orElseGet(() -> announcementReadRepository.save(AnnouncementRead.builder()
                        .announcement(announcement).student(student).readAt(LocalDateTime.now()).build()));
        return new StudentAnnouncement(announcement.getId(), announcement.getTitle(), announcement.getContent(),
                announcement.getAuthor() == null ? "Administracion" : displayName(announcement.getAuthor()),
                date(announcement.getPublishedAt()), true);
    }

    public StudentProfileDto studentProfile(User user) {
        StudentProfile student = student(user);
        Profile profile = student.getProfile();
        return new StudentProfileDto(student.getStudentCode(), profile.getFirstName(), profile.getLastName(), document(profile),
                user.getEmail(), level(student), grade(student.getGradeLevel(), level(student)), student.getSection(),
                nvl(student.getGuardianName()), nvl(student.getGuardianEmail()), nvl(student.getGuardianPhone()),
                student.getSchool() == null ? "" : student.getSchool().getName());
    }

    public StudentDashboard studentDashboard(User user) {
        StudentProfile student = student(user);
        List<StudentTask> tasks = studentTasks(user);
        List<StudentGrade> grades = studentGrades(user);
        List<StudentAnnouncement> announcements = studentAnnouncements(user);
        long pending = tasks.stream().filter(item -> "PENDIENTE".equals(item.estadoEntrega()) || "VENCIDA".equals(item.estadoEntrega())).count();
        long delivered = tasks.stream().filter(item -> "ENTREGADA".equals(item.estadoEntrega()) || "CALIFICADA".equals(item.estadoEntrega())).count();
        BigDecimal average = grades.stream().map(StudentGrade::nota).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!grades.isEmpty()) average = average.divide(BigDecimal.valueOf(grades.size()), 1, RoundingMode.HALF_UP);
        return new StudentDashboard(
                List.of(new Metric("Cursos activos", String.valueOf(studentCourseIds(student).size())),
                        new Metric("Tareas pendientes", String.valueOf(pending)),
                        new Metric("Entregas realizadas", String.valueOf(delivered)),
                        new Metric("Promedio", average.toPlainString())),
                tasks.stream().filter(item -> "PENDIENTE".equals(item.estadoEntrega()) || "VENCIDA".equals(item.estadoEntrega()))
                        .limit(5).map(item -> new StudentUpcomingTask(item.id(), item.titulo(), item.curso(), item.fechaEntrega(), item.estadoEntrega())).toList(),
                grades.stream().limit(5).map(item -> new StudentRecentGrade(item.id(), item.curso(), item.bimestre(), item.nota(), item.fechaRegistro())).toList(),
                announcements.stream().limit(5).map(item -> new StudentRecentAnnouncement(item.id(), item.titulo(), item.remitente(), item.fechaPublicacion())).toList()
        );
    }

    private TeacherProfile teacher(User user) {
        return teacherRepository.findByProfile_User_Id(user.getId())
                .orElseThrow(() -> notFound("Perfil docente no encontrado."));
    }

    private StudentProfile student(User user) {
        return studentRepository.findByProfile_User_Id(user.getId())
                .orElseThrow(() -> notFound("Perfil de alumno no encontrado."));
    }

    private Course teacherCourse(TeacherProfile teacher, Integer id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> notFound("Curso no encontrado."));
        if (course.getTeacher() == null || !teacher.getId().equals(course.getTeacher().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El curso no pertenece al docente autenticado.");
        }
        return course;
    }

    private Course studentCourse(StudentProfile student, Integer id) {
        if (!enrollmentRepository.existsByCourse_IdAndStudent_Id(id, student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El alumno no esta matriculado en el curso.");
        }
        return courseRepository.findById(id).orElseThrow(() -> notFound("Curso no encontrado."));
    }

    private AcademicTask teacherTask(TeacherProfile teacher, Integer id) {
        AcademicTask task = taskRepository.findById(id).orElseThrow(() -> notFound("Tarea no encontrada."));
        if (task.getTeacher() == null || !teacher.getId().equals(task.getTeacher().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La tarea no pertenece al docente autenticado.");
        }
        return task;
    }

    private AcademicTask studentTask(StudentProfile student, Integer id) {
        AcademicTask task = taskRepository.findById(id).orElseThrow(() -> notFound("Tarea no encontrada."));
        studentCourse(student, task.getCourse().getId());
        return task;
    }

    private GradeRecord teacherGrade(TeacherProfile teacher, Integer id) {
        GradeRecord grade = gradeRepository.findById(id).orElseThrow(() -> notFound("Nota no encontrada."));
        if (grade.getTeacher() == null || !teacher.getId().equals(grade.getTeacher().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La nota no pertenece al docente autenticado.");
        }
        return grade;
    }

    private Announcement teacherAnnouncement(User user, Integer id) {
        Announcement announcement = announcementRepository.findById(id).orElseThrow(() -> notFound("Comunicado no encontrado."));
        if (announcement.getAuthor() == null || !user.getId().equals(announcement.getAuthor().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El comunicado no pertenece al docente autenticado.");
        }
        return announcement;
    }

    private StudentProfile enrolledStudent(Course course, Integer studentId) {
        if (!enrollmentRepository.existsByCourse_IdAndStudent_Id(course.getId(), studentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno no esta matriculado en el curso seleccionado.");
        }
        return studentRepository.findById(studentId).orElseThrow(() -> notFound("Alumno no encontrado."));
    }

    private List<Integer> studentCourseIds(StudentProfile student) {
        return enrollmentRepository.findByStudent_Id(student.getId()).stream()
                .filter(item -> !"INACTIVO".equalsIgnoreCase(item.getStatus()))
                .map(item -> item.getCourse().getId()).toList();
    }

    private List<Announcement> studentAnnouncementsFor(StudentProfile student) {
        Set<Integer> courseIds = new HashSet<>(studentCourseIds(student));
        LocalDateTime now = LocalDateTime.now();
        return announcementRepository.findBySchool_IdOrderByPublishedAtDesc(student.getSchool().getId()).stream()
                .filter(item -> "PUBLICADO".equalsIgnoreCase(item.getStatus()))
                .filter(item -> item.getExpiresAt() == null || item.getExpiresAt().isAfter(now))
                .filter(item -> item.getCourse() == null || courseIds.contains(item.getCourse().getId()))
                .filter(item -> studentAudience(item.getAudience()))
                .toList();
    }

    private TeacherCourse toTeacherCourse(Course course) {
        return new TeacherCourse(course.getId(), course.getCode(), course.getName(), level(course),
                grade(course.getGradeLevel(), level(course)), course.getSection(), enrollmentRepository.findByCourse_Id(course.getId()).size(),
                "Ver calendario", Boolean.TRUE.equals(course.getIsActive()) ? "ACTIVO" : "INACTIVO");
    }

    private TeacherStudent toTeacherStudent(Enrollment enrollment, BigDecimal average) {
        StudentProfile student = enrollment.getStudent();
        Profile profile = student.getProfile();
        return new TeacherStudent(student.getId(), student.getStudentCode(), profile.getFirstName(), profile.getLastName(),
                document(profile), level(student), grade(student.getGradeLevel(), level(student)), student.getSection(),
                enrollment.getCourse().getName(), average,
                Boolean.TRUE.equals(profile.getIsActive()) && !"INACTIVO".equalsIgnoreCase(enrollment.getStatus()) ? "ACTIVO" : "INACTIVO");
    }

    private TeacherTask toTeacherTask(AcademicTask task) {
        Course course = task.getCourse();
        long total = enrollmentRepository.findByCourse_Id(course.getId()).size();
        long delivered = submissionRepository.countByTask_Id(task.getId());
        return new TeacherTask(task.getId(), task.getTitle(), nvl(task.getDescription()), course.getName(), course.getId(),
                level(course), grade(course.getGradeLevel(), level(course)), course.getSection(),
                date(task.getPublishedAt()), date(task.getDueAt()), task.getStatus(), delivered, Math.max(0, total - delivered));
    }

    private TeacherGrade toTeacherGrade(GradeRecord grade) {
        return new TeacherGrade(grade.getId(), grade.getStudent().getId(), grade.getCourse().getId(),
                grade.getStudent().getProfile().getFullName(), grade.getCourse().getName(), grade.getBimester(),
                grade.getScore(), nvl(grade.getObservation()), date(grade.getRegisteredAt()), grade.getStatus());
    }

    private TeacherAnnouncement toTeacherAnnouncement(Announcement announcement) {
        Course course = announcement.getCourse();
        return new TeacherAnnouncement(announcement.getId(), course == null ? null : course.getId(),
                announcement.getTitle(), announcement.getContent(), course == null ? "Todos" : course.getName(),
                course == null ? "Todos" : level(course), course == null ? "" : grade(course.getGradeLevel(), level(course)),
                course == null ? "" : course.getSection(), date(announcement.getPublishedAt()), announcement.getStatus());
    }

    private StudentCourse toStudentCourse(Course course, BigDecimal average, String enrollmentStatus) {
        return new StudentCourse(course.getId(), course.getCode(), course.getName(), teacherName(course),
                level(course), grade(course.getGradeLevel(), level(course)), course.getSection(), average,
                Boolean.TRUE.equals(course.getIsActive()) && !"INACTIVO".equalsIgnoreCase(enrollmentStatus) ? "ACTIVO" : "INACTIVO");
    }

    private StudentTask toStudentTask(AcademicTask task, HomeworkSubmission submission) {
        return new StudentTask(task.getId(), task.getTitle(), nvl(task.getDescription()), task.getCourse().getName(),
                teacherName(task.getCourse()), date(task.getPublishedAt()), date(task.getDueAt()), deliveryStatus(task, submission));
    }

    private SubmissionResponse toSubmission(HomeworkSubmission submission) {
        return new SubmissionResponse(submission.getId(), submission.getTask().getId(), submission.getStudent().getId(),
                submission.getStudent().getProfile().getFullName(), submission.getContent(), nvl(submission.getAttachmentUrl()),
                submission.getStatus(), submission.getScore(), nvl(submission.getFeedback()), date(submission.getSubmittedAt()));
    }

    private BigDecimal average(StudentProfile student, Course course) {
        List<BigDecimal> scores = gradeRepository.findByStudent_IdOrderByRegisteredAtDesc(student.getId()).stream()
                .filter(item -> item.getCourse().getId().equals(course.getId()))
                .filter(item -> !"ANULADA".equalsIgnoreCase(item.getStatus()))
                .map(GradeRecord::getScore).toList();
        if (scores.isEmpty()) return BigDecimal.ZERO;
        return scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);
    }

    private long pending(AcademicTask task) {
        return Math.max(0, enrollmentRepository.findByCourse_Id(task.getCourse().getId()).size()
                - submissionRepository.countByTask_Id(task.getId()));
    }

    private String deliveryStatus(AcademicTask task, HomeworkSubmission submission) {
        if (submission != null) return submission.getScore() != null ? "CALIFICADA" : "ENTREGADA";
        return task.getDueAt().isBefore(LocalDateTime.now()) ? "VENCIDA" : "PENDIENTE";
    }

    private String taskStatus(String value, String fallback) {
        return allowed(value, fallback, Set.of("BORRADOR", "PUBLICADA", "CERRADA"), "estado de tarea");
    }

    private String gradeStatus(String value, String fallback) {
        return allowed(value, fallback, Set.of("REGISTRADA", "PUBLICADA", "ANULADA"), "estado de nota");
    }

    private String announcementStatus(String value, String fallback) {
        return allowed(value, fallback, Set.of("BORRADOR", "PUBLICADO", "ARCHIVADO"), "estado de comunicado");
    }

    private String allowed(String value, String fallback, Set<String> values, String field) {
        String normalized = blankDefault(value, fallback).toUpperCase(Locale.ROOT);
        if (!values.contains(normalized)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor invalido para " + field + ".");
        return normalized;
    }

    private boolean studentAudience(String value) {
        String audience = nvl(value).toUpperCase(Locale.ROOT);
        return audience.isBlank() || audience.contains("TOD") || audience.contains("ALUM") || audience.contains("ESTUD");
    }

    private String teacherName(Course course) {
        return course.getTeacher() == null ? "Sin docente" : course.getTeacher().getProfile().getFullName();
    }

    private String displayName(User user) {
        return user.getProfile() == null ? blankDefault(user.getDisplayName(), user.getEmail()) : user.getProfile().getFullName();
    }

    private String aula(Course course) {
        return blankDefault(course.getRoom(), grade(course.getGradeLevel(), level(course)) + " " + course.getSection());
    }

    private String level(Course course) {
        return blankDefault(course.getEducationLevel(), "Primaria");
    }

    private String level(StudentProfile student) {
        return blankDefault(student.getEducationLevel(), "Primaria");
    }

    private String grade(Short value, String level) {
        return value == null ? "" : value + " " + level;
    }

    private String document(Profile profile) {
        if (profile == null || profile.getDocumentNumber() == null || profile.getDocumentNumber().isBlank()) return "";
        return blankDefault(profile.getDocumentType(), "DNI") + " " + profile.getDocumentNumber();
    }

    private String date(Object value) {
        if (value == null) return "";
        if (value instanceof LocalDateTime dateTime) return dateTime.toString();
        return value.toString();
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }

    private String blankDefault(String value, String fallback) {
        return value == null || value.isBlank() ? nvl(fallback) : value.trim();
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
