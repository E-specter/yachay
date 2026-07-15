package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminIdentityReadController {

    private static final String DEFAULT_SCHOOL_CODE = "MGP";

    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminIdentityReadController(
            StudentProfileRepository studentProfileRepository,
            TeacherProfileRepository teacherProfileRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository,
            RoleRepository roleRepository,
            SchoolRepository schoolRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.schoolRepository = schoolRepository;
    }

    @GetMapping("/usuarios")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminUserResponse>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toAdminUser)
                .toList());
    }

    @GetMapping("/usuarios/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AdminUserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(toAdminUser(findUser(id)));
    }

    @PostMapping("/usuarios")
    @Transactional
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        User user = createBaseUser(
                request.email(),
                resolvePassword(request.passwordTemporal(), request.password()),
                request.nombres(),
                request.apellidos(),
                request.rol()
        );

        setProfileActive(user, request.activo());
        return ResponseEntity.status(HttpStatus.CREATED).body(toAdminUser(user));
    }

    @PutMapping("/usuarios/{id}")
    @Transactional
    public ResponseEntity<AdminUserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateAdminUserRequest request) {
        User user = findUser(id);
        updateEmail(user, request.email());
        Profile profile = user.getProfile();
        profile.setFirstName(request.nombres().trim());
        profile.setLastName(request.apellidos().trim());
        profile.setIsActive(!"INACTIVO".equalsIgnoreCase(request.estado()));
        user.setDisplayName(profile.getFullName());
        Role role = roleRepository.findByName(request.rol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no encontrado."));
        user.setRoles(new HashSet<>(Set.of(role)));
        updatePasswordIfPresent(user, request.passwordTemporal());
        profileRepository.save(profile);
        return ResponseEntity.ok(toAdminUser(userRepository.save(user)));
    }

    @PatchMapping("/usuarios/{id}/estado")
    @Transactional
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        User user = findUser(id);
        setProfileActive(user, !"INACTIVO".equalsIgnoreCase(request.estado()));
        return ResponseEntity.ok(toAdminUser(user));
    }

    @PatchMapping("/usuarios/{id}/reset-password")
    @Transactional
    public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable Long id) {
        User user = findUser(id);
        String temporaryPassword = "Ych!" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        user.setEncryptedPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        return ResponseEntity.ok(new ResetPasswordResponse(temporaryPassword));
    }

    @GetMapping("/alumnos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminStudentResponse>> getStudents() {
        return ResponseEntity.ok(studentProfileRepository.findAll().stream()
                .sorted(Comparator.comparing(StudentProfile::getId))
                .map(this::toStudent)
                .toList());
    }

    @GetMapping("/alumnos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AdminStudentResponse> getStudent(@PathVariable Integer id) {
        return ResponseEntity.ok(toStudent(findStudent(id)));
    }

    @PostMapping("/alumnos")
    @Transactional
    public ResponseEntity<AdminStudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        if (studentProfileRepository.existsByStudentCode(request.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de estudiante ya existe.");
        }

        User user = createBaseUser(
                studentEmail(request),
                resolvePassword(request.passwordTemporal(), request.password()),
                request.nombres(),
                request.apellidos(),
                "ALUMNO"
        );

        School school = defaultSchool();
        Profile profile = user.getProfile();
        profile.setDocumentType(request.documentoTipo());
        profile.setDocumentNumber(request.documentoNumero());
        StudentProfile student = StudentProfile.builder()
                .profile(profile)
                .school(school)
                .studentCode(request.codigo())
                .gradeLevel(parseGradeLevel(request.grado()))
                .educationLevel(request.nivel() == null ? "Primaria" : request.nivel())
                .section(request.seccion())
                .enrollmentDate(request.fechaMatricula() != null ? request.fechaMatricula() : LocalDate.now())
                .guardianName(request.apoderado())
                .guardianEmail(request.correoApoderado())
                .guardianPhone(request.celularApoderado())
                .build();

        StudentProfile savedStudent = studentProfileRepository.save(student);
        profile.setStudentProfile(savedStudent);
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(toStudent(savedStudent));
    }

    @PutMapping("/alumnos/{id}")
    @Transactional
    public ResponseEntity<AdminStudentResponse> updateStudent(@PathVariable Integer id, @Valid @RequestBody CreateStudentRequest request) {
        StudentProfile student = findStudent(id);
        if (!Objects.equals(student.getStudentCode(), request.codigo()) && studentProfileRepository.existsByStudentCode(request.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de estudiante ya existe.");
        }
        Profile profile = student.getProfile();
        User user = profile.getUser();
        updateEmail(user, studentEmail(request));
        updatePasswordIfPresent(user, request.passwordTemporal() != null ? request.passwordTemporal() : request.password());
        profile.setFirstName(request.nombres().trim());
        profile.setLastName(request.apellidos().trim());
        profile.setDocumentType(request.documentoTipo());
        profile.setDocumentNumber(request.documentoNumero());
        profile.setIsActive(request.activo() == null ? !"INACTIVO".equalsIgnoreCase(request.estado()) : request.activo());
        user.setDisplayName(profile.getFullName());
        student.setStudentCode(request.codigo().trim());
        student.setEducationLevel(request.nivel() == null ? "Primaria" : request.nivel());
        student.setGradeLevel(parseGradeLevel(request.grado()));
        student.setSection(request.seccion());
        student.setEnrollmentDate(request.fechaMatricula() == null ? student.getEnrollmentDate() : request.fechaMatricula());
        student.setGuardianName(request.apoderado());
        student.setGuardianEmail(request.correoApoderado());
        student.setGuardianPhone(request.celularApoderado());
        userRepository.save(user);
        profileRepository.save(profile);
        return ResponseEntity.ok(toStudent(studentProfileRepository.save(student)));
    }

    @PatchMapping("/alumnos/{id}/estado")
    @Transactional
    public ResponseEntity<AdminStudentResponse> updateStudentStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request
    ) {
        StudentProfile student = findStudent(id);
        setProfileActive(student.getProfile(), !"INACTIVO".equalsIgnoreCase(request.estado()));
        return ResponseEntity.ok(toStudent(student));
    }

    @GetMapping("/docentes")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminTeacherResponse>> getTeachers() {
        return ResponseEntity.ok(teacherProfileRepository.findAll().stream()
                .sorted(Comparator.comparing(TeacherProfile::getId))
                .map(this::toTeacher)
                .toList());
    }

    @GetMapping("/docentes/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AdminTeacherResponse> getTeacher(@PathVariable Integer id) {
        return ResponseEntity.ok(toTeacher(findTeacher(id)));
    }

    @PostMapping("/docentes")
    @Transactional
    public ResponseEntity<AdminTeacherResponse> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        if (teacherProfileRepository.existsByEmployeeId(request.codigoEmpleado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de empleado ya existe.");
        }

        User user = createBaseUser(
                request.email(),
                resolvePassword(request.passwordTemporal(), request.password()),
                request.nombres(),
                request.apellidos(),
                "DOCENTE"
        );

        Profile profile = user.getProfile();
        profile.setDocumentType(request.documentoTipo());
        profile.setDocumentNumber(request.documentoNumero());
        user.setPhone(blankToNull(request.telefono()));
        TeacherProfile teacher = TeacherProfile.builder()
                .profile(profile)
                .school(defaultSchool())
                .employeeId(request.codigoEmpleado())
                .specialization(request.especialidad())
                .hireDate(request.fechaContratacion() != null ? request.fechaContratacion() : LocalDate.now())
                .build();

        TeacherProfile savedTeacher = teacherProfileRepository.save(teacher);
        profile.setTeacherProfile(savedTeacher);
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(toTeacher(savedTeacher));
    }

    @PutMapping("/docentes/{id}")
    @Transactional
    public ResponseEntity<AdminTeacherResponse> updateTeacher(@PathVariable Integer id, @Valid @RequestBody CreateTeacherRequest request) {
        TeacherProfile teacher = findTeacher(id);
        if (!Objects.equals(teacher.getEmployeeId(), request.codigoEmpleado()) && teacherProfileRepository.existsByEmployeeId(request.codigoEmpleado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de empleado ya existe.");
        }
        Profile profile = teacher.getProfile();
        User user = profile.getUser();
        updateEmail(user, request.email());
        updatePasswordIfPresent(user, request.passwordTemporal() != null ? request.passwordTemporal() : request.password());
        user.setPhone(blankToNull(request.telefono()));
        user.setDisplayName(request.nombres().trim() + " " + request.apellidos().trim());
        profile.setFirstName(request.nombres().trim());
        profile.setLastName(request.apellidos().trim());
        profile.setDocumentType(request.documentoTipo());
        profile.setDocumentNumber(request.documentoNumero());
        profile.setIsActive(request.activo() == null ? !"INACTIVO".equalsIgnoreCase(request.estado()) : request.activo());
        teacher.setEmployeeId(request.codigoEmpleado());
        teacher.setSpecialization(request.especialidad());
        teacher.setHireDate(request.fechaContratacion() == null ? teacher.getHireDate() : request.fechaContratacion());
        userRepository.save(user);
        profileRepository.save(profile);
        return ResponseEntity.ok(toTeacher(teacherProfileRepository.save(teacher)));
    }

    @PatchMapping("/docentes/{id}/estado")
    @Transactional
    public ResponseEntity<AdminTeacherResponse> updateTeacherStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request
    ) {
        TeacherProfile teacher = findTeacher(id);
        setProfileActive(teacher.getProfile(), !"INACTIVO".equalsIgnoreCase(request.estado()));
        return ResponseEntity.ok(toTeacher(teacher));
    }

    private User createBaseUser(String email, String password, String firstName, String lastName, String roleName) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no encontrado."));

        User user = User.builder()
                .email(normalizedEmail)
                .displayName(firstName + " " + lastName)
                .providerType("local")
                .encryptedPassword(passwordEncoder.encode(password))
                .emailConfirmedAt(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(role)))
                .build();

        User savedUser = userRepository.save(user);
        Profile profile = Profile.builder()
                .user(savedUser)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .build();

        Profile savedProfile = profileRepository.save(profile);
        savedUser.setProfile(savedProfile);
        return userRepository.save(savedUser);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private StudentProfile findStudent(Integer id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado."));
    }

    private TeacherProfile findTeacher(Integer id) {
        return teacherProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado."));
    }

    private School defaultSchool() {
        return schoolRepository.findByCode(DEFAULT_SCHOOL_CODE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Colegio MGP no existe."));
    }

    private String resolvePassword(String temporal, String password) {
        String value = temporal != null && !temporal.isBlank() ? temporal : password;
        if (value == null || value.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña temporal debe tener al menos 8 caracteres.");
        }
        return value;
    }

    private String studentEmail(CreateStudentRequest request) {
        String email = request.email() != null && !request.email().isBlank()
                ? request.email()
                : request.correoInstitucional();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo institucional es obligatorio.");
        }
        return email;
    }

    private void updateEmail(User user, String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmail(normalized)
                .filter(found -> !found.getId().equals(user.getId()))
                .ifPresent(found -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado."); });
        user.setEmail(normalized);
    }

    private void updatePasswordIfPresent(User user, String password) {
        if (password == null || password.isBlank()) return;
        if (password.length() < 8) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres.");
        user.setEncryptedPassword(passwordEncoder.encode(password));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Short parseGradeLevel(String grade) {
        if (grade == null || grade.isBlank()) return 3;
        String digits = grade.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return 3;
        return Short.valueOf(digits.substring(0, 1));
    }

    private void setProfileActive(User user, Boolean active) {
        Profile profile = user.getProfile();
        if (profile != null) {
            setProfileActive(profile, active);
        }
    }

    private void setProfileActive(Profile profile, Boolean active) {
        profile.setIsActive(active == null || active);
        profileRepository.save(profile);
    }

    private AdminUserResponse toAdminUser(User user) {
        Profile profile = user.getProfile();
        String role = user.getRoles().stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse("ALUMNO");

        return new AdminUserResponse(
                user.getId(),
                profile != null ? profile.getFirstName() : user.getDisplayName(),
                profile != null ? profile.getLastName() : "",
                user.getEmail(),
                role,
                profile == null || Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVO" : "INACTIVO",
                user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate().toString() : ""
        );
    }

    private AdminStudentResponse toStudent(StudentProfile student) {
        Profile profile = student.getProfile();
        User user = profile != null ? profile.getUser() : null;
        return new AdminStudentResponse(
                student.getId(),
                student.getStudentCode(),
                profile != null ? profile.getFirstName() : "",
                profile != null ? profile.getLastName() : "",
                profile != null && profile.getDocumentType() != null ? profile.getDocumentType() : "DNI",
                profile != null && profile.getDocumentNumber() != null ? profile.getDocumentNumber() : "",
                user != null ? user.getEmail() : "",
                student.getEducationLevel() == null ? "Primaria" : student.getEducationLevel(),
                student.getGradeLevel() + " " + (student.getEducationLevel() == null ? "Primaria" : student.getEducationLevel()),
                student.getSection(),
                profile == null || Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVO" : "INACTIVO",
                student.getGuardianName() == null ? "" : student.getGuardianName(),
                student.getGuardianEmail() == null ? "" : student.getGuardianEmail()
        );
    }

    private AdminTeacherResponse toTeacher(TeacherProfile teacher) {
        Profile profile = teacher.getProfile();
        User user = profile != null ? profile.getUser() : null;
        return new AdminTeacherResponse(
                teacher.getId(),
                profile != null ? profile.getFirstName() : "",
                profile != null ? profile.getLastName() : "",
                profile != null && profile.getDocumentType() != null ? profile.getDocumentType() : "DNI",
                profile != null && profile.getDocumentNumber() != null ? profile.getDocumentNumber() : "",
                user != null ? user.getEmail() : "",
                teacher.getSpecialization(),
                user != null && user.getPhone() != null ? user.getPhone() : "",
                profile == null || Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVO" : "INACTIVO",
                teacher.getCreatedAt() != null ? teacher.getCreatedAt().toLocalDate().toString() : ""
                , teacher.getEmployeeId(), teacher.getHireDate() != null ? teacher.getHireDate().toString() : ""
        );
    }

    public record AdminUserResponse(Long id, String nombres, String apellidos, String email, String rol, String estado, String fechaCreacion) {
    }

    public record AdminStudentResponse(Integer id, String codigo, String nombres, String apellidos, String documentoTipo, String documentoNumero, String correoInstitucional, String nivel, String grado, String seccion, String estado, String apoderado, String correoApoderado) {
    }

    public record AdminTeacherResponse(Integer id, String nombres, String apellidos, String documentoTipo, String documentoNumero, String email, String especialidad, String telefono, String estado, String fechaCreacion, String codigoEmpleado, String fechaContratacion) {
    }

    public record CreateAdminUserRequest(@NotBlank String nombres, @NotBlank String apellidos, @Email @NotBlank String email, String passwordTemporal, String password, @NotBlank String rol, Boolean activo) {
    }

    public record UpdateAdminUserRequest(@NotBlank String nombres, @NotBlank String apellidos, @Email @NotBlank String email, String passwordTemporal, @NotBlank String rol, @NotBlank String estado) {
    }

    public record CreateStudentRequest(@NotBlank String nombres, @NotBlank String apellidos, @Email String email, @Email String correoInstitucional, String passwordTemporal, String password, String documentoTipo, String documentoNumero, String nivel, @NotBlank String grado, @NotBlank String seccion, @NotBlank String codigo, LocalDate fechaMatricula, Boolean activo, String estado, String apoderado, @Email String correoApoderado, String celularApoderado) {
    }

    public record CreateTeacherRequest(@NotBlank String nombres, @NotBlank String apellidos, @Email @NotBlank String email, String passwordTemporal, String password, @NotBlank String codigoEmpleado, @NotBlank String especialidad, String documentoTipo, String documentoNumero, String telefono, LocalDate fechaContratacion, Boolean activo, String estado) {
    }

    public record UpdateStatusRequest(@NotNull String estado) {
    }

    public record ResetPasswordResponse(String temporaryPassword) {
    }
}
