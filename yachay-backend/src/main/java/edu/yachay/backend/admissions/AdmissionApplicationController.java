package edu.yachay.backend.admissions;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.admissions.dto.AdmissionApplicationResponse;
import edu.yachay.backend.admissions.dto.AdmissionDecisionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/admin/postulaciones")
public class AdmissionApplicationController {

    private final AdmissionApplicationRepository admissionApplicationRepository;

    public AdmissionApplicationController(AdmissionApplicationRepository admissionApplicationRepository) {
        this.admissionApplicationRepository = admissionApplicationRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdmissionApplicationResponse>> findAll() {
        List<AdmissionApplicationResponse> applications = admissionApplicationRepository.findAll().stream()
                .sorted(Comparator.comparing(AdmissionApplication::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AdmissionApplicationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(findApplication(id)));
    }

    @PatchMapping("/{id}/aceptar")
    @Transactional
    public ResponseEntity<AdmissionApplicationResponse> accept(
            @PathVariable Long id,
            @RequestBody(required = false) AdmissionDecisionRequest request
    ) {
        AdmissionApplication application = findApplication(id);
        application.setStatus("ACEPTADA");
        application.setObservations(resolveObservation(request, "Postulacion aceptada desde administracion."));

        return ResponseEntity.ok(toResponse(admissionApplicationRepository.save(application)));
    }

    @PatchMapping("/{id}/rechazar")
    @Transactional
    public ResponseEntity<AdmissionApplicationResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) AdmissionDecisionRequest request
    ) {
        AdmissionApplication application = findApplication(id);
        application.setStatus("RECHAZADA");
        application.setObservations(resolveObservation(request, "Postulacion rechazada desde administracion."));

        return ResponseEntity.ok(toResponse(admissionApplicationRepository.save(application)));
    }

    private AdmissionApplication findApplication(Long id) {
        return admissionApplicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada."));
    }

    private AdmissionApplicationResponse toResponse(AdmissionApplication application) {
        return new AdmissionApplicationResponse(
                application.getId(),
                application.studentFullName(),
                application.guardianFullName(),
                application.getGuardianPhone(),
                application.getGuardianEmail(),
                application.getLevel(),
                application.getGrade(),
                displayStatus(application.getStatus()),
                application.getStatus(),
                application.getObservations(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private String resolveObservation(AdmissionDecisionRequest request, String defaultObservation) {
        if (request == null) {
            return defaultObservation;
        }

        if (request.observaciones() != null && !request.observaciones().isBlank()) {
            return request.observaciones();
        }

        if (request.motivo() != null && !request.motivo().isBlank()) {
            return request.motivo();
        }

        return defaultObservation;
    }

    private String displayStatus(String status) {
        if (status == null || status.isBlank()) {
            return "Pendiente";
        }

        return switch (status.trim().toUpperCase()) {
            case "ACEPTADA" -> "Aceptada";
            case "RECHAZADA" -> "Rechazada";
            default -> "Pendiente";
        };
    }
}
