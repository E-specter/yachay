package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.TeacherProfileDTO;
import edu.yachay.backend.identity.application.dtos.CreateTeacherProfileRequest;
import edu.yachay.backend.identity.application.ports.inputs.TeacherProfileServicePort;
import edu.yachay.backend.identity.domain.exceptions.ResourceConflictException;
import edu.yachay.backend.identity.domain.exceptions.ResourceNotFoundException;
import edu.yachay.backend.identity.domain.models.TeacherProfile;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.repositories.TeacherProfileRepository;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación para gestionar perfiles de docente.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherProfileService implements TeacherProfileServicePort {

    private final TeacherProfileRepository teacherProfileRepository;
    private final ProfileRepository profileRepository;
    private final SchoolRepository schoolRepository;
    private final IdentityMapper mapper;

    @Override
    public TeacherProfileDTO createTeacherProfile(CreateTeacherProfileRequest request) {
        Profile profile = profileRepository.findByUserId(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Perfil para usuario " + request.getUserId() + " no encontrado"));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Escuela con ID " + request.getSchoolId() + " no encontrada"));

        if (request.getEmployeeId() != null && teacherProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new ResourceConflictException("El ID de empleado '" + request.getEmployeeId() + "' ya existe");
        }

        TeacherProfile teacherProfile = TeacherProfile.builder()
                .profile(profile)
                .school(school)
                .employeeId(request.getEmployeeId())
                .specialization(request.getSpecialization())
                .hireDate(request.getHireDate())
                .build();

        TeacherProfile savedTeacherProfile = teacherProfileRepository.save(teacherProfile);
        return mapper.toTeacherProfileDTO(savedTeacherProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeacherProfileDTO> getTeacherProfileById(Integer teacherProfileId) {
        return teacherProfileRepository.findById(teacherProfileId).map(mapper::toTeacherProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeacherProfileDTO> getTeacherProfileByEmployeeId(String employeeId) {
        return teacherProfileRepository.findByEmployeeId(employeeId).map(mapper::toTeacherProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherProfileDTO> getAllTeacherProfiles() {
        return teacherProfileRepository.findAll().stream()
                .map(mapper::toTeacherProfileDTO)
                .toList();
    }

    @Override
    public TeacherProfileDTO updateTeacherProfile(Integer teacherProfileId, CreateTeacherProfileRequest request) {
        TeacherProfile teacherProfile = teacherProfileRepository.findById(teacherProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de docente con ID " + teacherProfileId + " no encontrado"));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Escuela con ID " + request.getSchoolId() + " no encontrada"));

        if (request.getEmployeeId() != null && !request.getEmployeeId().equals(teacherProfile.getEmployeeId())) {
            if (teacherProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
                throw new ResourceConflictException("El ID de empleado '" + request.getEmployeeId() + "' ya existe");
            }
            teacherProfile.setEmployeeId(request.getEmployeeId());
        }

        teacherProfile.setSchool(school);
        teacherProfile.setSpecialization(request.getSpecialization());
        teacherProfile.setHireDate(request.getHireDate());

        TeacherProfile updatedTeacherProfile = teacherProfileRepository.save(teacherProfile);
        return mapper.toTeacherProfileDTO(updatedTeacherProfile);
    }

    @Override
    public void deleteTeacherProfile(Integer teacherProfileId) {
        if (!teacherProfileRepository.existsById(teacherProfileId)) {
            throw new ResourceNotFoundException("Perfil de docente con ID " + teacherProfileId + " no encontrado");
        }
        teacherProfileRepository.deleteById(teacherProfileId);
    }
}
