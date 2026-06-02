package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.StudentProfileDTO;
import edu.yachay.backend.identity.application.dtos.CreateStudentProfileRequest;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con perfiles de estudiante.
 */
public interface StudentProfileServicePort {
    StudentProfileDTO createStudentProfile(CreateStudentProfileRequest request);

    Optional<StudentProfileDTO> getStudentProfileById(Integer studentProfileId);

    Optional<StudentProfileDTO> getStudentProfileByCode(String studentCode);

    List<StudentProfileDTO> getAllStudentProfiles();

    StudentProfileDTO updateStudentProfile(Integer studentProfileId, CreateStudentProfileRequest request);

    void deleteStudentProfile(Integer studentProfileId);
}
