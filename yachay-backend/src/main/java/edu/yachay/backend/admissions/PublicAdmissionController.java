package edu.yachay.backend.admissions;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.admissions.dto.CreateAdmissionRequest;
import edu.yachay.backend.admissions.dto.CreateAdmissionResponse;
import edu.yachay.backend.notification.PersistentNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admisiones")
public class PublicAdmissionController {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final PersistentNotificationService persistentNotificationService;

    public PublicAdmissionController(
            AdmissionApplicationRepository admissionApplicationRepository,
            PersistentNotificationService persistentNotificationService
    ) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.persistentNotificationService = persistentNotificationService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CreateAdmissionResponse> create(
            @Valid @RequestBody CreateAdmissionRequest request
    ) {
        List<AdmissionApplication> applications = new ArrayList<>();
        String guardianFirstName = normalize(request.apoderado().nombres());
        String guardianLastName = joinNames(
                request.apoderado().apellidoPaterno(),
                request.apoderado().apellidoMaterno()
        );

        for (CreateAdmissionRequest.ApplicantRequest applicant : request.postulantes()) {
            String studentFirstName = normalize(applicant.nombres());
            String studentLastName = joinNames(applicant.apellidoPaterno(), applicant.apellidoMaterno());
            String guardianEmail = normalize(request.apoderado().correo()).toLowerCase();

            if (admissionApplicationRepository.existsByStudentFirstNameAndStudentLastNameAndGuardianEmail(
                    studentFirstName,
                    studentLastName,
                    guardianEmail
            )) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Ya existe una postulacion para el estudiante y apoderado indicados."
                );
            }

            applications.add(AdmissionApplication.builder()
                    .studentFirstName(studentFirstName)
                    .studentLastName(studentLastName)
                    .guardianFirstName(guardianFirstName)
                    .guardianLastName(guardianLastName)
                    .guardianPhone(preferredPhone(request.apoderado()))
                    .guardianEmail(guardianEmail)
                    .level(normalize(applicant.nivel()))
                    .grade(normalize(applicant.grado()))
                    .status("PENDIENTE")
                    .build());
        }

        List<AdmissionApplication> savedApplications = admissionApplicationRepository.saveAll(applications);
        savedApplications.forEach(application -> persistentNotificationService.createForRole(
                "ADMINISTRADOR",
                "Nueva postulacion registrada",
                application.studentFullName() + " postula a " + application.getGrade(),
                "ADMISION",
                "/admin/postulaciones"
        ));

        AdmissionApplication first = savedApplications.get(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAdmissionResponse(
                first.getId(),
                first.getStatus(),
                first.getCreatedAt()
        ));
    }

    private String preferredPhone(CreateAdmissionRequest.GuardianRequest guardian) {
        if (guardian.celular() != null && !guardian.celular().isBlank()) {
            return guardian.celular().trim();
        }
        return guardian.telefono() != null ? guardian.telefono().trim() : null;
    }

    private String joinNames(String first, String second) {
        return (normalize(first) + " " + normalize(second)).trim();
    }

    private String normalize(String value) {
        return value != null ? value.trim() : "";
    }
}
