package edu.yachay.backend.admissions;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.admissions.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/admin/postulaciones")
public class AdmissionApplicationController {

    private final AdmissionApplicationRepository admissionApplicationRepository;
    private final AdmissionDecisionService decisionService;

    public AdmissionApplicationController(
            AdmissionApplicationRepository admissionApplicationRepository,
            AdmissionDecisionService decisionService
    ) {
        this.admissionApplicationRepository = admissionApplicationRepository;
        this.decisionService = decisionService;
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
        return ResponseEntity.ok(toResponse(decisionService.accept(id, request)));
    }

    @PatchMapping("/{id}/rechazar")
    @Transactional
    public ResponseEntity<AdmissionApplicationResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) AdmissionDecisionRequest request
    ) {
        return ResponseEntity.ok(toResponse(decisionService.reject(id, request)));
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
