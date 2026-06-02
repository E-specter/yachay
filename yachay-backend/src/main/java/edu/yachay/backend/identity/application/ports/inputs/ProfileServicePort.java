package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.*;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con perfiles.
 */
public interface ProfileServicePort {
    ProfileDTO getProfileByUserId(String userId);

    Optional<ProfileDTO> getProfileById(Integer profileId);

    List<ProfileDTO> getAllProfiles();

    void updateProfile(String userId, UpdateUserRequest request);

    void deleteProfile(Integer profileId);
}
