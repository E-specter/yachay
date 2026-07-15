package edu.yachay.backend.admissions;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.admissions.dto.AdmissionDecisionRequest;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.*;
import edu.yachay.backend.notification.NotificationService;
import edu.yachay.backend.notification.PersistentNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AdmissionDecisionService {
    private final AdmissionApplicationRepository applications;
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final StudentProfileRepository students;
    private final SchoolRepository schools;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final PersistentNotificationService persistentNotifications;

    public AdmissionDecisionService(AdmissionApplicationRepository applications, UserRepository users,
                                    ProfileRepository profiles, StudentProfileRepository students,
                                    SchoolRepository schools, RoleRepository roles, PasswordEncoder passwordEncoder,
                                    NotificationService notificationService,
                                    PersistentNotificationService persistentNotifications) {
        this.applications = applications;
        this.users = users;
        this.profiles = profiles;
        this.students = students;
        this.schools = schools;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.persistentNotifications = persistentNotifications;
    }

    @Transactional
    public AdmissionApplication accept(Long id, AdmissionDecisionRequest request) {
        AdmissionApplication application = find(id);
        String level = value(request == null ? null : request.nivel(), application.getLevel());
        String grade = value(request == null ? null : request.grado(), application.getGrade());
        String section = value(request == null ? null : request.seccion(), "A");
        application.setLevel(level);
        application.setGrade(grade);
        application.setStatus("ACEPTADA");
        application.setObservations(observation(request, "Postulacion aceptada desde administracion."));
        application.setDecidedAt(LocalDateTime.now());

        boolean generate = request == null || request.generarCredenciales() == null || request.generarCredenciales();
        if (generate) {
            StudentProfile student = application.getStudentProfile();
            if (student == null) {
                student = createStudent(application, level, grade, section);
                application.setStudentProfile(student);
            } else {
                student.setEducationLevel(level);
                student.setGradeLevel(parseGrade(grade));
                student.setSection(section);
                students.save(student);
            }
            if (request == null || request.enviarCorreo() == null || request.enviarCorreo()) {
                notificationService.sendAdmissionAcceptedEmail(application.getGuardianEmail(), student.getProfile().getUser().getEmail());
            }
        }
        AdmissionApplication saved = applications.save(application);
        persistentNotifications.createForRole("ADMINISTRADOR", "Postulacion aceptada",
                application.studentFullName() + " fue aceptado para " + grade, "ADMISION", "/admin/postulaciones");
        return saved;
    }

    @Transactional
    public AdmissionApplication reject(Long id, AdmissionDecisionRequest request) {
        AdmissionApplication application = find(id);
        application.setStatus("RECHAZADA");
        application.setObservations(observation(request, "Postulacion rechazada desde administracion."));
        application.setDecidedAt(LocalDateTime.now());
        if (request == null || request.enviarCorreo() == null || request.enviarCorreo()) {
            notificationService.sendAdmissionRejectedEmail(application.getGuardianEmail(), application.getObservations());
        }
        persistentNotifications.createForRole("ADMINISTRADOR", "Postulacion rechazada",
                application.studentFullName() + " fue rechazado para " + application.getGrade(), "ADMISION", "/admin/postulaciones");
        return applications.save(application);
    }

    private StudentProfile createStudent(AdmissionApplication application, String level, String grade, String section) {
        School school = schools.findByCode("MGP").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Colegio MGP no existe."));
        Role role = roles.findByName("ALUMNO").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol ALUMNO no existe."));
        String email = uniqueEmail(application);
        String temporaryPassword = "Ych!" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        User user = users.save(User.builder().email(email).displayName(application.studentFullName()).providerType("local")
                .encryptedPassword(passwordEncoder.encode(temporaryPassword)).emailConfirmedAt(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(role))).build());
        Profile profile = profiles.save(Profile.builder().user(user).firstName(application.getStudentFirstName())
                .lastName(application.getStudentLastName()).isActive(true).build());
        user.setProfile(profile);
        users.save(user);
        StudentProfile student = students.save(StudentProfile.builder().profile(profile).school(school)
                .studentCode(uniqueStudentCode(application.getId())).educationLevel(level).gradeLevel(parseGrade(grade))
                .section(section).enrollmentDate(LocalDate.now()).guardianName(application.guardianFullName())
                .guardianEmail(application.getGuardianEmail()).guardianPhone(application.getGuardianPhone()).build());
        profile.setStudentProfile(student);
        profiles.save(profile);
        return student;
    }

    private String uniqueEmail(AdmissionApplication application) {
        String base = normalize(application.getStudentFirstName()).split(" ")[0] + "." + normalize(application.getStudentLastName()).replace(" ", ".");
        String email = base + "@mgp.edu.pe";
        if (!users.existsByEmail(email)) return email;
        return base + "." + application.getId() + "@mgp.edu.pe";
    }

    private String uniqueStudentCode(Long id) {
        String code = "ALU-2026-" + String.format("%04d", id);
        return students.existsByStudentCode(code) ? code + "-A" : code;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replaceAll("[^a-z0-9 ]", "").trim();
    }

    private Short parseGrade(String grade) {
        String digits = grade == null ? "" : grade.replaceAll("[^0-9]", "");
        return digits.isBlank() ? 1 : Short.valueOf(digits.substring(0, 1));
    }

    private String observation(AdmissionDecisionRequest request, String fallback) {
        if (request == null) return fallback;
        return value(request.observaciones(), value(request.motivo(), fallback));
    }

    private String value(String candidate, String fallback) {
        return candidate == null || candidate.isBlank() ? fallback : candidate.trim();
    }

    private AdmissionApplication find(Long id) {
        return applications.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada."));
    }
}
