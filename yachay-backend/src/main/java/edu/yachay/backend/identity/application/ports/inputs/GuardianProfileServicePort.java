package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.GuardianProfileDTO;
import edu.yachay.backend.identity.application.dtos.CreateGuardianProfileRequest;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con perfiles de apoderado/tutor.
 */
public interface GuardianProfileServicePort {
    GuardianProfileDTO createGuardianProfile(CreateGuardianProfileRequest request);

    Optional<GuardianProfileDTO> getGuardianProfileById(Integer guardianProfileId);

    List<GuardianProfileDTO> getAllGuardianProfiles();

    GuardianProfileDTO updateGuardianProfile(Integer guardianProfileId, CreateGuardianProfileRequest request);

    void deleteGuardianProfile(Integer guardianProfileId);

    void assignStudentToGuardian(Integer guardianProfileId, Integer studentProfileId);

    void removeStudentFromGuardian(Integer guardianProfileId, Integer studentProfileId);
}
