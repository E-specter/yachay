package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.SchoolDTO;
import edu.yachay.backend.identity.application.dtos.SchoolRequest;
import edu.yachay.backend.identity.application.ports.inputs.SchoolServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Adaptador REST para gestionar escuelas.
 */
@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolServicePort schoolService;

    @PostMapping
    public ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolRequest request) {
        SchoolDTO schoolDTO = schoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolDTO);
    }

    @PutMapping("/{schoolId}")
    public ResponseEntity<SchoolDTO> updateSchool(
            @PathVariable Integer schoolId,
            @Valid @RequestBody SchoolRequest request) {
        SchoolDTO schoolDTO = schoolService.updateSchool(schoolId, request);
        return ResponseEntity.ok(schoolDTO);
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<?> getSchoolById(@PathVariable Integer schoolId) {
        var school = schoolService.getSchoolById(schoolId);
        if (school.isPresent()) {
            return ResponseEntity.ok(school.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getSchoolByCode(@PathVariable String code) {
        var school = schoolService.getSchoolByCode(code);
        if (school.isPresent()) {
            return ResponseEntity.ok(school.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<SchoolDTO>> getAllSchools() {
        List<SchoolDTO> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(schools);
    }

    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Void> deleteSchool(@PathVariable Integer schoolId) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.noContent().build();
    }
}
