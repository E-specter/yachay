package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.GuardianProfileDTO;
import edu.yachay.backend.identity.application.dtos.CreateGuardianProfileRequest;
import edu.yachay.backend.identity.application.ports.inputs.GuardianProfileServicePort;
import edu.yachay.backend.identity.domain.exceptions.ResourceNotFoundException;
import edu.yachay.backend.identity.domain.models.GuardianProfile;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.repositories.GuardianProfileRepository;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación para gestionar perfiles de apoderado/tutor.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GuardianProfileService implements GuardianProfileServicePort {

    private final GuardianProfileRepository guardianProfileRepository;
    private final ProfileRepository profileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final IdentityMapper mapper;

    @Override
    public GuardianProfileDTO createGuardianProfile(CreateGuardianProfileRequest request) {
        Profile profile = profileRepository.findByUserId(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Perfil para usuario " + request.getUserId() + " no encontrado"));

        GuardianProfile guardianProfile = GuardianProfile.builder()
                .profile(profile)
                .relationship(request.getRelationship())
                .occupation(request.getOccupation())
                .build();

        GuardianProfile savedGuardianProfile = guardianProfileRepository.save(guardianProfile);
        return mapper.toGuardianProfileDTO(savedGuardianProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GuardianProfileDTO> getGuardianProfileById(Integer guardianProfileId) {
        return guardianProfileRepository.findById(guardianProfileId).map(mapper::toGuardianProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuardianProfileDTO> getAllGuardianProfiles() {
        return guardianProfileRepository.findAll().stream()
                .map(mapper::toGuardianProfileDTO)
                .toList();
    }

    @Override
    public GuardianProfileDTO updateGuardianProfile(Integer guardianProfileId, CreateGuardianProfileRequest request) {
        GuardianProfile guardianProfile = guardianProfileRepository.findById(guardianProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de apoderado con ID " + guardianProfileId + " no encontrado"));

        guardianProfile.setRelationship(request.getRelationship());
        guardianProfile.setOccupation(request.getOccupation());

        GuardianProfile updatedGuardianProfile = guardianProfileRepository.save(guardianProfile);
        return mapper.toGuardianProfileDTO(updatedGuardianProfile);
    }

    @Override
    public void deleteGuardianProfile(Integer guardianProfileId) {
        if (!guardianProfileRepository.existsById(guardianProfileId)) {
            throw new ResourceNotFoundException("Perfil de apoderado con ID " + guardianProfileId + " no encontrado");
        }
        guardianProfileRepository.deleteById(guardianProfileId);
    }

    @Override
    public void assignStudentToGuardian(Integer guardianProfileId, Integer studentProfileId) {
        GuardianProfile guardian = guardianProfileRepository.findById(guardianProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de apoderado con ID " + guardianProfileId + " no encontrado"));

        StudentProfile student = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de estudiante con ID " + studentProfileId + " no encontrado"));

        guardian.addStudent(student);
        guardianProfileRepository.save(guardian);
    }

    @Override
    public void removeStudentFromGuardian(Integer guardianProfileId, Integer studentProfileId) {
        GuardianProfile guardian = guardianProfileRepository.findById(guardianProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de apoderado con ID " + guardianProfileId + " no encontrado"));

        StudentProfile student = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de estudiante con ID " + studentProfileId + " no encontrado"));

        guardian.removeStudent(student);
        guardianProfileRepository.save(guardian);
    }
}
