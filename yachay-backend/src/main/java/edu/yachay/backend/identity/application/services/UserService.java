package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.UserServicePort;
import edu.yachay.backend.identity.domain.exceptions.*;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserServicePort {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final IdentityMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("El email '" + request.getEmail() + "' ya esta registrado");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new ResourceConflictException("El telefono '" + request.getPhone() + "' ya esta registrado");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .displayName(request.getFirstName() + " " + request.getLastName())
                .encryptedPassword(passwordEncoder.encode(request.getPassword()))
                .build();

        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            Set<Role> roles = roleRepository.findByNameIn(request.getRoleNames());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(savedUser)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .avatarUrl(request.getAvatarUrl())
                .isActive(true)
                .build();

        profileRepository.save(profile);
        savedUser.setProfile(profile);

        return mapper.toUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(parseUserId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceConflictException("El email '" + request.getEmail() + "' ya esta registrado");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new ResourceConflictException("El telefono '" + request.getPhone() + "' ya esta registrado");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getFirstName() != null || request.getLastName() != null) {
            String firstName = request.getFirstName() != null ? request.getFirstName() : user.getProfile().getFirstName();
            String lastName = request.getLastName() != null ? request.getLastName() : user.getProfile().getLastName();
            user.setDisplayName(firstName + " " + lastName);

            Profile profile = user.getProfile();
            if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
            if (request.getLastName() != null) profile.setLastName(request.getLastName());
            if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
            if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
            if (request.getIsActive() != null) profile.setIsActive(request.getIsActive());

            profileRepository.save(profile);
        }

        User updatedUser = userRepository.save(user);
        return mapper.toUserDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(parseUserId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado"));
        return mapper.toUserDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(mapper::toUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String userId) {
        Long id = parseUserId(userId);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void assignRoleToUser(String userId, String roleName) {
        User user = userRepository.findById(parseUserId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol '" + roleName + "' no encontrado"));

        if (user.getRoles().contains(role)) {
            throw new ResourceConflictException("El usuario ya tiene el rol '" + roleName + "'");
        }

        user.addRole(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(String userId, String roleName) {
        User user = userRepository.findById(parseUserId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol '" + roleName + "' no encontrado"));

        if (!user.getRoles().contains(role)) {
            throw new ResourceNotFoundException("El usuario no tiene el rol '" + roleName + "'");
        }

        user.removeRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol '" + roleName + "' no encontrado"));

        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .map(mapper::toUserDTO)
                .collect(Collectors.toList());
    }

    private Long parseUserId(String userId) {
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException exception) {
            throw new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado");
        }
    }
}
