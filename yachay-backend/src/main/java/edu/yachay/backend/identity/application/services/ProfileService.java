package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.ProfileServicePort;
import edu.yachay.backend.identity.domain.exceptions.ResourceNotFoundException;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación para gestionar perfiles.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService implements ProfileServicePort {

    private final ProfileRepository profileRepository;
    private final IdentityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileDTO getProfileByUserId(String userId) {
        UUID uuid = UUID.fromString(userId);
        Profile profile = profileRepository.findByUserId(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil para usuario " + userId + " no encontrado"));
        return mapper.toProfileDTO(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileDTO> getProfileById(Integer profileId) {
        return profileRepository.findById(profileId).map(mapper::toProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileDTO> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(mapper::toProfileDTO)
                .toList();
    }

    @Override
    public void updateProfile(String userId, UpdateUserRequest request) {
        UUID uuid = UUID.fromString(userId);
        Profile profile = profileRepository.findByUserId(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil para usuario " + userId + " no encontrado"));

        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getIsActive() != null) profile.setIsActive(request.getIsActive());

        profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(Integer profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Perfil con ID " + profileId + " no encontrado");
        }
        profileRepository.deleteById(profileId);
    }
}
