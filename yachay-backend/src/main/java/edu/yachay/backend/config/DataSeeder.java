package edu.yachay.backend.config;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.academic.domain.models.AcademicTask;
import edu.yachay.backend.academic.domain.models.AcademicYear;
import edu.yachay.backend.academic.domain.models.Announcement;
import edu.yachay.backend.academic.domain.models.CalendarEvent;
import edu.yachay.backend.academic.domain.models.Course;
import edu.yachay.backend.academic.domain.models.Enrollment;
import edu.yachay.backend.academic.domain.models.GradeRecord;
import edu.yachay.backend.academic.domain.models.Subject;
import edu.yachay.backend.academic.domain.repositories.AcademicTaskRepository;
import edu.yachay.backend.academic.domain.repositories.AcademicYearRepository;
import edu.yachay.backend.academic.domain.repositories.AnnouncementRepository;
import edu.yachay.backend.academic.domain.repositories.CalendarEventRepository;
import edu.yachay.backend.academic.domain.repositories.CourseRepository;
import edu.yachay.backend.academic.domain.repositories.EnrollmentRepository;
import edu.yachay.backend.academic.domain.repositories.GradeRecordRepository;
import edu.yachay.backend.academic.domain.repositories.SubjectRepository;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.models.TeacherProfile;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import edu.yachay.backend.identity.domain.repositories.RoleRepository;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import edu.yachay.backend.identity.domain.repositories.TeacherProfileRepository;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import edu.yachay.backend.notification.PersistentNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicTaskRepository academicTaskRepository;
    private final GradeRecordRepository gradeRecordRepository;
    private final AnnouncementRepository announcementRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final PersistentNotificationService persistentNotificationService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(
            RoleRepository roleRepository,
            SchoolRepository schoolRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository,
            TeacherProfileRepository teacherProfileRepository,
            StudentProfileRepository studentProfileRepository,
            AcademicYearRepository academicYearRepository,
            SubjectRepository subjectRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AcademicTaskRepository academicTaskRepository,
            GradeRecordRepository gradeRecordRepository,
            AnnouncementRepository announcementRepository,
            CalendarEventRepository calendarEventRepository,
            AdmissionApplicationRepository admissionApplicationRepository,
            PersistentNotificationService persistentNotificationService
    ) {
        this.roleRepository = roleRepository;
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.academicYearRepository = academicYearRepository;
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.academicTaskRepository = academicTaskRepository;
        this.gradeRecordRepository = gradeRecordRepository;
        this.announcementRepository = announcementRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.persistentNotificationService = persistentNotificationService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Role> roles = seedRoles();
        School school = seedSchool();
        seedAdmin(roles);
        Map<String, TeacherProfile> teachers = seedTeachers(roles, school);
        List<StudentProfile> students = seedStudents(roles, school);
        seedAcademicData(school, teachers, students);
        seedAdmissionApplications();
        seedNotifications();
    }

    private Map<String, Role> seedRoles() {
        Map<String, String> definitions = new LinkedHashMap<>();
        definitions.put("ADMINISTRADOR", "Gestiona la configuracion institucional, usuarios y procesos administrativos.");
        definitions.put("DOCENTE", "Gestiona cursos, tareas, calificaciones y comunicados academicos.");
        definitions.put("ALUMNO", "Accede a cursos, calendario, tareas, notas y comunicados.");
        definitions.put("APODERADO", "Responsable legal y contacto de notificaciones del estudiante.");

        Map<String, Role> roles = new LinkedHashMap<>();
        definitions.forEach((name, description) -> {
            Role role = roleRepository.findByName(name)
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(name)
                            .description(description)
                            .build()));

            if (!description.equals(role.getDescription())) {
                role.setDescription(description);
                role = roleRepository.save(role);
            }

            roles.put(name, role);
        });

        log.info("DataSeeder: roles verificados");
        return roles;
    }

    private School seedSchool() {
        School school = schoolRepository.findByCode("MGP")
                .orElseGet(() -> schoolRepository.save(School.builder()
                        .name("Colegio Manuel Gonzales Prada")
                        .code("MGP")
                        .address("Lima, Peru")
                        .phone("999999999")
                        .isActive(true)
                        .build()));

        school.setName("Colegio Manuel Gonzales Prada");
        school.setAddress("Lima, Peru");
        school.setPhone("999999999");
        school.setIsActive(true);
        School savedSchool = schoolRepository.save(school);

        log.info("DataSeeder: colegio verificado");
        return savedSchool;
    }

    private void seedAdmin(Map<String, Role> roles) {
        User admin = ensureUser(
                new SeedUser(
                        "admin@yachay.edu.pe",
                        "Admin123456",
                        "Administrador",
                        "Yachay",
                        "Administrador Yachay",
                        roles.get("ADMINISTRADOR")
                )
        );
        ensureProfile(admin, "Administrador", "Yachay");
        log.info("DataSeeder: admin verificado");
    }

    private Map<String, TeacherProfile> seedTeachers(Map<String, Role> roles, School school) {
        List<SeedTeacher> definitions = List.of(
                new SeedTeacher("docente1@yachay.edu.pe", "Ana", "Torres", "DOC-2026-001", "Matematica"),
                new SeedTeacher("docente2@yachay.edu.pe", "Luis", "Ramirez", "DOC-2026-002", "Comunicacion"),
                new SeedTeacher("docente3@yachay.edu.pe", "Carla", "Mendoza", "DOC-2026-003", "Ciencia y Tecnologia")
        );

        Map<String, TeacherProfile> teachers = new LinkedHashMap<>();
        for (SeedTeacher definition : definitions) {
            User user = ensureUser(new SeedUser(
                    definition.email(),
                    "Docente123456",
                    definition.firstName(),
                    definition.lastName(),
                    definition.firstName() + " " + definition.lastName(),
                    roles.get("DOCENTE")
            ));

            Profile profile = ensureProfile(user, definition.firstName(), definition.lastName());
            TeacherProfile teacherProfile = ensureTeacherProfile(profile, school, definition.employeeId(), definition.specialization());
            teachers.put(definition.email(), teacherProfile);
        }

        log.info("DataSeeder: docentes verificados");
        return teachers;
    }

    private List<StudentProfile> seedStudents(Map<String, Role> roles, School school) {
        List<SeedStudent> definitions = List.of(
                new SeedStudent("alumno1@yachay.edu.pe", "Maria Fernanda", "Salazar Rojas", "ALU-2026-001", (short) 3, "A"),
                new SeedStudent("alumno2@yachay.edu.pe", "Joaquin Andres", "Paredes Leon", "ALU-2026-002", (short) 3, "A"),
                new SeedStudent("alumno3@yachay.edu.pe", "Valentina", "Quispe Flores", "ALU-2026-003", (short) 3, "A"),
                new SeedStudent("alumno4@yachay.edu.pe", "Sebastian", "Mendoza Ruiz", "ALU-2026-004", (short) 3, "A"),
                new SeedStudent("alumno5@yachay.edu.pe", "Camila", "Torres Medina", "ALU-2026-005", (short) 3, "A")
        );

        List<StudentProfile> students = new ArrayList<>();
        for (SeedStudent definition : definitions) {
            User user = ensureUser(new SeedUser(
                    definition.email(),
                    "Alumno123456",
                    definition.firstName(),
                    definition.lastName(),
                    definition.firstName() + " " + definition.lastName(),
                    roles.get("ALUMNO")
            ));

            Profile profile = ensureProfile(user, definition.firstName(), definition.lastName());
            StudentProfile studentProfile = ensureStudentProfile(
                    profile,
                    school,
                    definition.studentCode(),
                    definition.gradeLevel(),
                    definition.section()
            );
            students.add(studentProfile);
        }

        log.info("DataSeeder: alumnos verificados");
        return students;
    }

    private void seedAcademicData(School school, Map<String, TeacherProfile> teachers, List<StudentProfile> students) {
        AcademicYear academicYear = academicYearRepository.findByYear(2026)
                .orElseGet(() -> academicYearRepository.save(AcademicYear.builder()
                        .year(2026)
                        .startDate(LocalDate.of(2026, 3, 2))
                        .endDate(LocalDate.of(2026, 12, 18))
                        .isActive(true)
                        .build()));
        academicYear.setIsActive(true);

        Subject mathematics = ensureSubject("MAT", "Matematica", "Matematica");
        Subject communication = ensureSubject("COM", "Comunicacion", "Comunicacion");
        Subject science = ensureSubject("CYT", "Ciencia y Tecnologia", "Ciencia y Tecnologia");
        Subject social = ensureSubject("PSO", "Personal Social", "Ciencias Sociales");

        Course mathCourse = ensureCourse("MGP-2026-MAT-3A", "Matematica III", school, academicYear, mathematics, teachers.get("docente1@yachay.edu.pe"));
        Course communicationCourse = ensureCourse("MGP-2026-COM-3A", "Comunicacion I", school, academicYear, communication, teachers.get("docente2@yachay.edu.pe"));
        Course scienceCourse = ensureCourse("MGP-2026-CYT-3A", "Ciencia y Tecnologia", school, academicYear, science, teachers.get("docente3@yachay.edu.pe"));
        Course socialCourse = ensureCourse("MGP-2026-PSO-3A", "Personal Social", school, academicYear, social, teachers.get("docente2@yachay.edu.pe"));

        List<Course> courses = List.of(mathCourse, communicationCourse, scienceCourse, socialCourse);
        for (StudentProfile student : students) {
            for (Course course : courses) {
                ensureEnrollment(course, student);
            }
        }

        ensureTask(mathCourse, "Resolucion de problemas", "Resolver problemas de operaciones combinadas.", LocalDateTime.of(2026, 5, 5, 8, 0), LocalDateTime.of(2026, 5, 12, 23, 59));
        ensureTask(communicationCourse, "Lectura comprensiva", "Leer el texto asignado y responder las preguntas.", LocalDateTime.of(2026, 5, 6, 9, 0), LocalDateTime.of(2026, 5, 13, 23, 59));
        ensureTask(scienceCourse, "Informe de laboratorio", "Preparar conclusiones sobre el experimento de mezclas.", LocalDateTime.of(2026, 5, 7, 10, 0), LocalDateTime.of(2026, 5, 15, 23, 59));

        seedGrades(students, mathCourse, communicationCourse, scienceCourse);
        ensureAnnouncement(school, null, "Entrega de libretas", "La entrega de libretas se realizara el viernes en secretaria.", "ALUMNOS_APODERADOS");
        ensureAnnouncement(school, mathCourse, "Reforzamiento de operaciones", "El curso de Matematica tendra material adicional disponible.", "ALUMNOS");

        seedCalendar(mathCourse, communicationCourse, scienceCourse, socialCourse, students.get(0));
        log.info("DataSeeder: datos academicos verificados");
    }

    private void seedAdmissionApplications() {
        List<SeedAdmissionApplication> applications = List.of(
                new SeedAdmissionApplication(
                        "Maria Fernanda",
                        "Salazar Rojas",
                        "Rosa",
                        "Rojas Perez",
                        "987654321",
                        "rosa.rojas@example.com",
                        "Primaria",
                        "3 Primaria",
                        "PENDIENTE",
                        "Solicitud pendiente de revision administrativa."
                ),
                new SeedAdmissionApplication(
                        "Diego Alonso",
                        "Torres Medina",
                        "Luis",
                        "Torres Vega",
                        "987654322",
                        "luis.torres@example.com",
                        "Primaria",
                        "4 Primaria",
                        "PENDIENTE",
                        "Documentos registrados para evaluacion."
                ),
                new SeedAdmissionApplication(
                        "Valeria Camila",
                        "Huaman Flores",
                        "Carmen",
                        "Flores Diaz",
                        "987654323",
                        "carmen.flores@example.com",
                        "Secundaria",
                        "1 Secundaria",
                        "ACEPTADA",
                        "Postulacion aceptada. Pendiente completar asignacion final de aula."
                ),
                new SeedAdmissionApplication(
                        "Joaquin Gabriel",
                        "Ramos Soto",
                        "Elena",
                        "Soto Paredes",
                        "987654324",
                        "elena.soto@example.com",
                        "Primaria",
                        "5 Primaria",
                        "RECHAZADA",
                        "No alcanzo vacante disponible para el grado solicitado."
                ),
                new SeedAdmissionApplication(
                        "Luciana Andrea",
                        "Perez Castillo",
                        "Miguel",
                        "Perez Leon",
                        "987654325",
                        "miguel.perez@example.com",
                        "Secundaria",
                        "2 Secundaria",
                        "PENDIENTE",
                        "Solicitud recibida desde admision publica."
                )
        );

        for (SeedAdmissionApplication application : applications) {
            if (admissionApplicationRepository.existsByStudentFirstNameAndStudentLastNameAndGuardianEmail(
                    application.studentFirstName(),
                    application.studentLastName(),
                    application.guardianEmail()
            )) {
                continue;
            }

            admissionApplicationRepository.save(AdmissionApplication.builder()
                    .studentFirstName(application.studentFirstName())
                    .studentLastName(application.studentLastName())
                    .guardianFirstName(application.guardianFirstName())
                    .guardianLastName(application.guardianLastName())
                    .guardianPhone(application.guardianPhone())
                    .guardianEmail(application.guardianEmail())
                    .level(application.level())
                    .grade(application.grade())
                    .status(application.status())
                    .observations(application.observations())
                    .build());
        }

        log.info("DataSeeder: postulaciones verificadas");
    }

    private User ensureUser(SeedUser seed) {
        User user = userRepository.findByEmail(seed.email())
                .orElseGet(() -> User.builder()
                        .email(seed.email())
                        .displayName(seed.displayName())
                        .providerType("local")
                        .encryptedPassword(passwordEncoder.encode(seed.password()))
                        .emailConfirmedAt(LocalDateTime.now())
                        .roles(new java.util.HashSet<>())
                        .build());

        user.setDisplayName(seed.displayName());
        user.setProviderType("local");
        if (user.getEmailConfirmedAt() == null) {
            user.setEmailConfirmedAt(LocalDateTime.now());
        }
        if (!passwordMatches(seed.password(), user.getEncryptedPassword())) {
            user.setEncryptedPassword(passwordEncoder.encode(seed.password()));
        }
        if (user.getRoles() == null) {
            user.setRoles(new java.util.HashSet<>());
        }
        user.getRoles().add(seed.role());

        return userRepository.save(user);
    }

    private boolean passwordMatches(String rawPassword, String encryptedPassword) {
        if (!StringUtils.hasText(encryptedPassword)) {
            return false;
        }

        try {
            return passwordEncoder.matches(rawPassword, encryptedPassword);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private Profile ensureProfile(User user, String firstName, String lastName) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> Profile.builder()
                        .user(user)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build());

        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setIsActive(true);
        Profile savedProfile = profileRepository.save(profile);

        user.setProfile(savedProfile);
        userRepository.save(user);

        return savedProfile;
    }

    private TeacherProfile ensureTeacherProfile(Profile profile, School school, String employeeId, String specialization) {
        TeacherProfile teacherProfile = teacherProfileRepository.findByEmployeeId(employeeId)
                .or(() -> teacherProfileRepository.findByProfileId(profile.getId()))
                .orElseGet(() -> TeacherProfile.builder()
                        .profile(profile)
                        .school(school)
                        .employeeId(employeeId)
                        .specialization(specialization)
                        .hireDate(LocalDate.of(2026, 2, 1))
                        .build());

        teacherProfile.setProfile(profile);
        teacherProfile.setSchool(school);
        teacherProfile.setEmployeeId(employeeId);
        teacherProfile.setSpecialization(specialization);
        teacherProfile.setHireDate(LocalDate.of(2026, 2, 1));
        return teacherProfileRepository.save(teacherProfile);
    }

    private StudentProfile ensureStudentProfile(Profile profile, School school, String studentCode, Short gradeLevel, String section) {
        StudentProfile studentProfile = studentProfileRepository.findByStudentCode(studentCode)
                .or(() -> studentProfileRepository.findByProfileId(profile.getId()))
                .orElseGet(() -> StudentProfile.builder()
                        .profile(profile)
                        .school(school)
                        .studentCode(studentCode)
                        .gradeLevel(gradeLevel)
                        .section(section)
                        .enrollmentDate(LocalDate.of(2026, 3, 2))
                        .build());

        studentProfile.setProfile(profile);
        studentProfile.setSchool(school);
        studentProfile.setStudentCode(studentCode);
        studentProfile.setGradeLevel(gradeLevel);
        studentProfile.setSection(section);
        studentProfile.setEnrollmentDate(LocalDate.of(2026, 3, 2));
        return studentProfileRepository.save(studentProfile);
    }

    private Subject ensureSubject(String code, String name, String area) {
        Subject subject = subjectRepository.findByCode(code)
                .orElseGet(() -> subjectRepository.save(Subject.builder()
                        .code(code)
                        .name(name)
                        .area(area)
                        .isActive(true)
                        .build()));

        subject.setName(name);
        subject.setArea(area);
        subject.setIsActive(true);
        return subjectRepository.save(subject);
    }

    private Course ensureCourse(String code, String name, School school, AcademicYear academicYear, Subject subject, TeacherProfile teacher) {
        Course course = courseRepository.findByCode(code)
                .orElseGet(() -> Course.builder()
                        .code(code)
                        .name(name)
                        .school(school)
                        .academicYear(academicYear)
                        .subject(subject)
                        .teacher(teacher)
                        .gradeLevel((short) 3)
                        .section("A")
                        .isActive(true)
                        .build());

        course.setName(name);
        course.setSchool(school);
        course.setAcademicYear(academicYear);
        course.setSubject(subject);
        course.setTeacher(teacher);
        course.setGradeLevel((short) 3);
        course.setSection("A");
        course.setIsActive(true);
        return courseRepository.save(course);
    }

    private void ensureEnrollment(Course course, StudentProfile student) {
        if (enrollmentRepository.existsByCourse_IdAndStudent_Id(course.getId(), student.getId())) {
            return;
        }

        enrollmentRepository.save(Enrollment.builder()
                .course(course)
                .student(student)
                .enrollmentDate(LocalDate.of(2026, 3, 2))
                .status("ACTIVO")
                .build());
    }

    private void ensureTask(Course course, String title, String description, LocalDateTime publishedAt, LocalDateTime dueAt) {
        if (academicTaskRepository.existsByCourse_IdAndTitle(course.getId(), title)) {
            return;
        }

        academicTaskRepository.save(AcademicTask.builder()
                .course(course)
                .teacher(course.getTeacher())
                .title(title)
                .description(description)
                .publishedAt(publishedAt)
                .dueAt(dueAt)
                .status("PUBLICADA")
                .build());
    }

    private void seedGrades(List<StudentProfile> students, Course mathCourse, Course communicationCourse, Course scienceCourse) {
        BigDecimal[] mathScores = {
                BigDecimal.valueOf(17),
                BigDecimal.valueOf(16),
                BigDecimal.valueOf(18),
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(17)
        };

        for (int index = 0; index < students.size(); index++) {
            ensureGrade(mathCourse, students.get(index), "I", mathScores[index], "Avance satisfactorio en resolucion de problemas.");
        }

        ensureGrade(communicationCourse, students.get(0), "I", BigDecimal.valueOf(16), "Buena comprension lectora.");
        ensureGrade(scienceCourse, students.get(0), "I", BigDecimal.valueOf(18), "Participacion destacada en laboratorio.");
    }

    private void ensureGrade(Course course, StudentProfile student, String bimester, BigDecimal score, String observation) {
        if (gradeRecordRepository.existsByCourse_IdAndStudent_IdAndBimester(course.getId(), student.getId(), bimester)) {
            return;
        }

        gradeRecordRepository.save(GradeRecord.builder()
                .course(course)
                .student(student)
                .teacher(course.getTeacher())
                .bimester(bimester)
                .score(score)
                .observation(observation)
                .registeredAt(LocalDateTime.of(2026, 5, 20, 9, 0))
                .status("REGISTRADA")
                .build());
    }

    private void ensureAnnouncement(School school, Course course, String title, String content, String audience) {
        if (announcementRepository.existsByTitleAndSchool_Code(title, school.getCode())) {
            return;
        }

        announcementRepository.save(Announcement.builder()
                .school(school)
                .course(course)
                .title(title)
                .content(content)
                .audience(audience)
                .publishedAt(LocalDateTime.of(2026, 5, 10, 8, 30))
                .status("PUBLICADO")
                .build());
    }

    private void seedCalendar(Course mathCourse, Course communicationCourse, Course scienceCourse, Course socialCourse, StudentProfile student) {
        ensureCalendarEvent(mathCourse, student, "Matematica III", "CURSO", "LUNES", null, LocalTime.of(8, 0), LocalTime.of(9, 0), "Clase semanal de Matematica.");
        ensureCalendarEvent(communicationCourse, student, "Comunicacion I", "CURSO", "LUNES", null, LocalTime.of(9, 0), LocalTime.of(10, 0), "Clase semanal de Comunicacion.");
        ensureCalendarEvent(scienceCourse, student, "Ciencia y Tecnologia", "CURSO", "LUNES", null, LocalTime.of(10, 0), LocalTime.of(11, 0), "Clase semanal de Ciencia y Tecnologia.");
        ensureCalendarEvent(socialCourse, student, "Personal Social", "CURSO", "MARTES", null, LocalTime.of(8, 0), LocalTime.of(9, 0), "Clase semanal de Personal Social.");
        ensureCalendarEvent(mathCourse, student, "Tarea: Resolucion de problemas", "TAREA", "MARTES", LocalDate.of(2026, 5, 12), LocalTime.of(13, 0), LocalTime.of(14, 0), "Fecha limite de entrega de tarea.");
        ensureCalendarEvent(communicationCourse, student, "Evaluacion: Lectura evaluada", "EVALUACION", "MIERCOLES", LocalDate.of(2026, 5, 13), LocalTime.of(10, 0), LocalTime.of(11, 0), "Evaluacion de comprension lectora.");
        ensureCalendarEvent(null, student, "Comunicado: Entrega de libretas", "COMUNICADO", "VIERNES", LocalDate.of(2026, 5, 15), LocalTime.of(12, 0), LocalTime.of(13, 0), "Comunicado administrativo para alumnos y apoderados.");
    }

    private void ensureCalendarEvent(
            Course course,
            StudentProfile student,
            String title,
            String eventType,
            String dayOfWeek,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String description
    ) {
        if (calendarEventRepository.existsByTitleAndDayOfWeekAndStartTime(title, dayOfWeek, startTime)) {
            return;
        }

        calendarEventRepository.save(CalendarEvent.builder()
                .course(course)
                .student(student)
                .title(title)
                .eventType(eventType)
                .audience(resolveAudience(eventType))
                .dayOfWeek(dayOfWeek)
                .eventDate(eventDate)
                .startTime(startTime)
                .endTime(endTime)
                .description(description)
                .build());
    }

    private String resolveAudience(String eventType) {
        if ("COMUNICADO".equalsIgnoreCase(eventType)) {
            return "TODOS";
        }

        if ("CURSO".equalsIgnoreCase(eventType) || "TAREA".equalsIgnoreCase(eventType) || "EVALUACION".equalsIgnoreCase(eventType)) {
            return "ALUMNO";
        }

        return "TODOS";
    }

    private void seedNotifications() {
        persistentNotificationService.createForRole(
                "ADMINISTRADOR",
                "Nueva postulacion recibida",
                "Hay solicitudes de admision pendientes por revisar.",
                "ADMISION",
                "/admin/postulaciones"
        );
        persistentNotificationService.createForRole(
                "ADMINISTRADOR",
                "Reporte de alumnos disponible",
                "El reporte XLSX de alumnos ya puede descargarse desde administracion.",
                "REPORTE",
                "/admin/alumnos"
        );
        persistentNotificationService.createForRole(
                "ADMINISTRADOR",
                "PDF de postulacion habilitado",
                "Las fichas PDF de postulacion se generan desde datos reales.",
                "DOCUMENTO",
                "/admin/postulaciones"
        );
        persistentNotificationService.createForRole(
                "DOCENTE",
                "Nueva tarea asignada al curso",
                "Revisa las tareas publicadas para tus cursos asignados.",
                "TAREA",
                "/docente/tareas"
        );
        persistentNotificationService.createForRole(
                "DOCENTE",
                "Comunicado institucional publicado",
                "Hay un comunicado institucional disponible para revisar.",
                "COMUNICADO",
                "/docente/comunicados"
        );
        persistentNotificationService.createForRole(
                "ALUMNO",
                "Nueva nota registrada",
                "Ya puedes revisar tus notas actualizadas.",
                "NOTA",
                "/alumno/notas"
        );
        persistentNotificationService.createForRole(
                "ALUMNO",
                "Evento academico programado",
                "Tu calendario academico tiene eventos próximos.",
                "CALENDARIO",
                "/alumno/calendario"
        );
        persistentNotificationService.createForRole(
                "ALUMNO",
                "Nueva tarea disponible",
                "Hay una tarea publicada para tus cursos.",
                "TAREA",
                "/alumno/tareas"
        );

        log.info("DataSeeder: notificaciones verificadas");
    }

    private record SeedUser(
            String email,
            String password,
            String firstName,
            String lastName,
            String displayName,
            Role role
    ) {
    }

    private record SeedTeacher(
            String email,
            String firstName,
            String lastName,
            String employeeId,
            String specialization
    ) {
    }

    private record SeedStudent(
            String email,
            String firstName,
            String lastName,
            String studentCode,
            Short gradeLevel,
            String section
    ) {
    }

    private record SeedAdmissionApplication(
            String studentFirstName,
            String studentLastName,
            String guardianFirstName,
            String guardianLastName,
            String guardianPhone,
            String guardianEmail,
            String level,
            String grade,
            String status,
            String observations
    ) {
    }
}
