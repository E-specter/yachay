package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.ProfileServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Adaptador REST para gestionar perfiles.
 * Expone endpoints para obtener y actualizar perfiles de usuario.
 */
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileServicePort profileService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileDTO> getProfileByUserId(@PathVariable String userId) {
        ProfileDTO profileDTO = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profileDTO);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<?> getProfileById(@PathVariable Integer profileId) {
        var profile = profileService.getProfileById(profileId);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        List<ProfileDTO> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<Void> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        profileService.updateProfile(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Integer profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }
}
