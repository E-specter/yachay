package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.TeacherProfileDTO;
import edu.yachay.backend.identity.application.dtos.CreateTeacherProfileRequest;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con perfiles de docente.
 */
public interface TeacherProfileServicePort {
    TeacherProfileDTO createTeacherProfile(CreateTeacherProfileRequest request);

    Optional<TeacherProfileDTO> getTeacherProfileById(Integer teacherProfileId);

    Optional<TeacherProfileDTO> getTeacherProfileByEmployeeId(String employeeId);

    List<TeacherProfileDTO> getAllTeacherProfiles();

    TeacherProfileDTO updateTeacherProfile(Integer teacherProfileId, CreateTeacherProfileRequest request);

    void deleteTeacherProfile(Integer teacherProfileId);
}
