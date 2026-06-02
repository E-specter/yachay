package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.TeacherProfileServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Adaptador REST para gestionar perfiles de docente.
 */
@RestController
@RequestMapping("/teacher-profiles")
@RequiredArgsConstructor
public class TeacherProfileController {

    private final TeacherProfileServicePort teacherProfileService;

    @PostMapping
    public ResponseEntity<TeacherProfileDTO> createTeacherProfile(
            @Valid @RequestBody CreateTeacherProfileRequest request) {
        TeacherProfileDTO teacherProfileDTO = teacherProfileService.createTeacherProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherProfileDTO);
    }

    @GetMapping("/{teacherProfileId}")
    public ResponseEntity<?> getTeacherProfileById(@PathVariable Integer teacherProfileId) {
        var teacherProfile = teacherProfileService.getTeacherProfileById(teacherProfileId);
        if (teacherProfile.isPresent()) {
            return ResponseEntity.ok(teacherProfile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getTeacherProfileByEmployeeId(@PathVariable String employeeId) {
        var teacherProfile = teacherProfileService.getTeacherProfileByEmployeeId(employeeId);
        if (teacherProfile.isPresent()) {
            return ResponseEntity.ok(teacherProfile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<TeacherProfileDTO>> getAllTeacherProfiles() {
        List<TeacherProfileDTO> teacherProfiles = teacherProfileService.getAllTeacherProfiles();
        return ResponseEntity.ok(teacherProfiles);
    }

    @PutMapping("/{teacherProfileId}")
    public ResponseEntity<TeacherProfileDTO> updateTeacherProfile(
            @PathVariable Integer teacherProfileId,
            @Valid @RequestBody CreateTeacherProfileRequest request) {
        TeacherProfileDTO teacherProfileDTO = teacherProfileService.updateTeacherProfile(teacherProfileId, request);
        return ResponseEntity.ok(teacherProfileDTO);
    }

    @DeleteMapping("/{teacherProfileId}")
    public ResponseEntity<Void> deleteTeacherProfile(@PathVariable Integer teacherProfileId) {
        teacherProfileService.deleteTeacherProfile(teacherProfileId);
        return ResponseEntity.noContent().build();
    }
}
