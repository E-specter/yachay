package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.CreateStudentProfileRequest;
import edu.yachay.backend.identity.application.dtos.StudentProfileDTO;
import edu.yachay.backend.identity.application.ports.inputs.StudentProfileServicePort;
import edu.yachay.backend.identity.domain.exceptions.ResourceConflictException;
import edu.yachay.backend.identity.domain.exceptions.ResourceNotFoundException;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.repositories.ProfileRepository;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentProfileService implements StudentProfileServicePort {

    private final StudentProfileRepository studentProfileRepository;
    private final ProfileRepository profileRepository;
    private final SchoolRepository schoolRepository;
    private final IdentityMapper mapper;

    @Override
    public StudentProfileDTO createStudentProfile(CreateStudentProfileRequest request) {
        Profile profile = profileRepository.findByUserId(parseUserId(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Perfil para usuario " + request.getUserId() + " no encontrado"));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Escuela con ID " + request.getSchoolId() + " no encontrada"));

        if (request.getStudentCode() != null && studentProfileRepository.existsByStudentCode(request.getStudentCode())) {
            throw new ResourceConflictException("El codigo de estudiante '" + request.getStudentCode() + "' ya existe");
        }

        StudentProfile studentProfile = StudentProfile.builder()
                .profile(profile)
                .school(school)
                .studentCode(request.getStudentCode())
                .gradeLevel(request.getGradeLevel())
                .section(request.getSection())
                .enrollmentDate(request.getEnrollmentDate())
                .build();

        StudentProfile savedStudentProfile = studentProfileRepository.save(studentProfile);
        return mapper.toStudentProfileDTO(savedStudentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentProfileDTO> getStudentProfileById(Integer studentProfileId) {
        return studentProfileRepository.findById(studentProfileId).map(mapper::toStudentProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentProfileDTO> getStudentProfileByCode(String studentCode) {
        return studentProfileRepository.findByStudentCode(studentCode).map(mapper::toStudentProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileDTO> getAllStudentProfiles() {
        return studentProfileRepository.findAll().stream()
                .map(mapper::toStudentProfileDTO)
                .toList();
    }

    @Override
    public StudentProfileDTO updateStudentProfile(Integer studentProfileId, CreateStudentProfileRequest request) {
        StudentProfile studentProfile = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de estudiante con ID " + studentProfileId + " no encontrado"));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Escuela con ID " + request.getSchoolId() + " no encontrada"));

        if (request.getStudentCode() != null && !request.getStudentCode().equals(studentProfile.getStudentCode())) {
            if (studentProfileRepository.existsByStudentCode(request.getStudentCode())) {
                throw new ResourceConflictException("El codigo de estudiante '" + request.getStudentCode() + "' ya existe");
            }
            studentProfile.setStudentCode(request.getStudentCode());
        }

        studentProfile.setSchool(school);
        studentProfile.setGradeLevel(request.getGradeLevel());
        studentProfile.setSection(request.getSection());
        studentProfile.setEnrollmentDate(request.getEnrollmentDate());

        StudentProfile updatedStudentProfile = studentProfileRepository.save(studentProfile);
        return mapper.toStudentProfileDTO(updatedStudentProfile);
    }

    @Override
    public void deleteStudentProfile(Integer studentProfileId) {
        if (!studentProfileRepository.existsById(studentProfileId)) {
            throw new ResourceNotFoundException("Perfil de estudiante con ID " + studentProfileId + " no encontrado");
        }
        studentProfileRepository.deleteById(studentProfileId);
    }

    private Long parseUserId(String userId) {
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException exception) {
            throw new ResourceNotFoundException("Perfil para usuario " + userId + " no encontrado");
        }
    }
}
