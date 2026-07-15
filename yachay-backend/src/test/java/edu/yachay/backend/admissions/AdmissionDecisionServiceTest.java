package edu.yachay.backend.admissions;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.admissions.dto.AdmissionDecisionRequest;
import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import edu.yachay.backend.identity.domain.repositories.RoleRepository;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import edu.yachay.backend.notification.NotificationService;
import edu.yachay.backend.notification.PersistentNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdmissionDecisionServiceTest {

    @Test
    void acceptCreatesAndLinksStudentCredentials() {
        AdmissionApplicationRepository applications = mock(AdmissionApplicationRepository.class);
        UserRepository users = mock(UserRepository.class);
        ProfileRepository profiles = mock(ProfileRepository.class);
        StudentProfileRepository students = mock(StudentProfileRepository.class);
        SchoolRepository schools = mock(SchoolRepository.class);
        RoleRepository roles = mock(RoleRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        NotificationService email = mock(NotificationService.class);
        PersistentNotificationService notifications = mock(PersistentNotificationService.class);
        AdmissionApplication application = pendingApplication(21L);

        when(applications.findById(21L)).thenReturn(Optional.of(application));
        when(applications.save(any(AdmissionApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(schools.findByCode("MGP")).thenReturn(Optional.of(School.builder().id(1).code("MGP").build()));
        when(roles.findByName("ALUMNO")).thenReturn(Optional.of(Role.builder().id(3).name("ALUMNO").build()));
        when(users.existsByEmail(anyString())).thenReturn(false);
        when(students.existsByStudentCode(anyString())).thenReturn(false);
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(profiles.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(students.save(any(StudentProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(encoder.encode(anyString())).thenReturn("bcrypt-hash");
        AdmissionDecisionService service = new AdmissionDecisionService(
                applications, users, profiles, students, schools, roles, encoder, email, notifications);

        AdmissionApplication accepted = service.accept(21L,
                new AdmissionDecisionRequest("Documentos conformes", null, "Primaria", "3 Primaria", "B", true, false));

        assertThat(accepted.getStatus()).isEqualTo("ACEPTADA");
        assertThat(accepted.getDecidedAt()).isNotNull();
        assertThat(accepted.getStudentProfile()).isNotNull();
        assertThat(accepted.getStudentProfile().getStudentCode()).isEqualTo("ALU-2026-0021");
        assertThat(accepted.getStudentProfile().getProfile().getUser().getEncryptedPassword()).isEqualTo("bcrypt-hash");
        assertThat(accepted.getStudentProfile().getSection()).isEqualTo("B");
        verify(applications).save(application);
        verify(email, never()).sendAdmissionAcceptedEmail(anyString(), anyString());
        verify(notifications).createForRole("ADMINISTRADOR", "Postulacion aceptada",
                "Ana Torres fue aceptado para 3 Primaria", "ADMISION", "/admin/postulaciones");
    }

    @Test
    void rejectPersistsReasonAndDecisionDate() {
        AdmissionApplicationRepository applications = mock(AdmissionApplicationRepository.class);
        NotificationService email = mock(NotificationService.class);
        PersistentNotificationService notifications = mock(PersistentNotificationService.class);
        AdmissionApplication application = pendingApplication(22L);
        when(applications.findById(22L)).thenReturn(Optional.of(application));
        when(applications.save(any(AdmissionApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AdmissionDecisionService service = new AdmissionDecisionService(
                applications, mock(UserRepository.class), mock(ProfileRepository.class),
                mock(StudentProfileRepository.class), mock(SchoolRepository.class), mock(RoleRepository.class),
                mock(PasswordEncoder.class), email, notifications);

        AdmissionApplication rejected = service.reject(22L,
                new AdmissionDecisionRequest(null, "Vacantes agotadas", null, null, null, false, false));

        assertThat(rejected.getStatus()).isEqualTo("RECHAZADA");
        assertThat(rejected.getObservations()).isEqualTo("Vacantes agotadas");
        assertThat(rejected.getDecidedAt()).isNotNull();
        verify(applications).save(application);
        verify(email, never()).sendAdmissionRejectedEmail(anyString(), anyString());
        verify(notifications).createForRole("ADMINISTRADOR", "Postulacion rechazada",
                "Ana Torres fue rechazado para 3 Primaria", "ADMISION", "/admin/postulaciones");
    }

    private AdmissionApplication pendingApplication(Long id) {
        return AdmissionApplication.builder()
                .id(id)
                .studentFirstName("Ana")
                .studentLastName("Torres")
                .guardianFirstName("Rosa")
                .guardianLastName("Torres")
                .guardianEmail("apoderado@example.test")
                .guardianPhone("999000111")
                .level("Primaria")
                .grade("3 Primaria")
                .status("PENDIENTE")
                .build();
    }
}
