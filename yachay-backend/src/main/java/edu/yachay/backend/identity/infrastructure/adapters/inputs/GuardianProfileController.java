package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.GuardianProfileServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Adaptador REST para gestionar perfiles de apoderado/tutor.
 */
@RestController
@RequestMapping("/guardian-profiles")
@RequiredArgsConstructor
public class GuardianProfileController {

    private final GuardianProfileServicePort guardianProfileService;

    @PostMapping
    public ResponseEntity<GuardianProfileDTO> createGuardianProfile(
            @Valid @RequestBody CreateGuardianProfileRequest request) {
        GuardianProfileDTO guardianProfileDTO = guardianProfileService.createGuardianProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardianProfileDTO);
    }

    @GetMapping("/{guardianProfileId}")
    public ResponseEntity<?> getGuardianProfileById(@PathVariable Integer guardianProfileId) {
        var guardianProfile = guardianProfileService.getGuardianProfileById(guardianProfileId);
        if (guardianProfile.isPresent()) {
            return ResponseEntity.ok(guardianProfile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<GuardianProfileDTO>> getAllGuardianProfiles() {
        List<GuardianProfileDTO> guardianProfiles = guardianProfileService.getAllGuardianProfiles();
        return ResponseEntity.ok(guardianProfiles);
    }

    @PutMapping("/{guardianProfileId}")
    public ResponseEntity<GuardianProfileDTO> updateGuardianProfile(
            @PathVariable Integer guardianProfileId,
            @Valid @RequestBody CreateGuardianProfileRequest request) {
        GuardianProfileDTO guardianProfileDTO = guardianProfileService.updateGuardianProfile(guardianProfileId, request);
        return ResponseEntity.ok(guardianProfileDTO);
    }

    @DeleteMapping("/{guardianProfileId}")
    public ResponseEntity<Void> deleteGuardianProfile(@PathVariable Integer guardianProfileId) {
        guardianProfileService.deleteGuardianProfile(guardianProfileId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{guardianProfileId}/students/{studentProfileId}")
    public ResponseEntity<Void> assignStudentToGuardian(
            @PathVariable Integer guardianProfileId,
            @PathVariable Integer studentProfileId) {
        guardianProfileService.assignStudentToGuardian(guardianProfileId, studentProfileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{guardianProfileId}/students/{studentProfileId}")
    public ResponseEntity<Void> removeStudentFromGuardian(
            @PathVariable Integer guardianProfileId,
            @PathVariable Integer studentProfileId) {
        guardianProfileService.removeStudentFromGuardian(guardianProfileId, studentProfileId);
        return ResponseEntity.noContent().build();
    }
}
