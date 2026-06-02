package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.StudentProfileServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Adaptador REST para gestionar perfiles de estudiante.
 */
@RestController
@RequestMapping("/student-profiles")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileServicePort studentProfileService;

    @PostMapping
    public ResponseEntity<StudentProfileDTO> createStudentProfile(
            @Valid @RequestBody CreateStudentProfileRequest request) {
        StudentProfileDTO studentProfileDTO = studentProfileService.createStudentProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentProfileDTO);
    }

    @GetMapping("/{studentProfileId}")
    public ResponseEntity<?> getStudentProfileById(@PathVariable Integer studentProfileId) {
        var studentProfile = studentProfileService.getStudentProfileById(studentProfileId);
        if (studentProfile.isPresent()) {
            return ResponseEntity.ok(studentProfile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/code/{studentCode}")
    public ResponseEntity<?> getStudentProfileByCode(@PathVariable String studentCode) {
        var studentProfile = studentProfileService.getStudentProfileByCode(studentCode);
        if (studentProfile.isPresent()) {
            return ResponseEntity.ok(studentProfile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<StudentProfileDTO>> getAllStudentProfiles() {
        List<StudentProfileDTO> studentProfiles = studentProfileService.getAllStudentProfiles();
        return ResponseEntity.ok(studentProfiles);
    }

    @PutMapping("/{studentProfileId}")
    public ResponseEntity<StudentProfileDTO> updateStudentProfile(
            @PathVariable Integer studentProfileId,
            @Valid @RequestBody CreateStudentProfileRequest request) {
        StudentProfileDTO studentProfileDTO = studentProfileService.updateStudentProfile(studentProfileId, request);
        return ResponseEntity.ok(studentProfileDTO);
    }

    @DeleteMapping("/{studentProfileId}")
    public ResponseEntity<Void> deleteStudentProfile(@PathVariable Integer studentProfileId) {
        studentProfileService.deleteStudentProfile(studentProfileId);
        return ResponseEntity.noContent().build();
    }
}
