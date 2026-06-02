package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.SchoolDTO;
import edu.yachay.backend.identity.application.dtos.SchoolRequest;
import edu.yachay.backend.identity.application.ports.inputs.SchoolServicePort;
import edu.yachay.backend.identity.domain.exceptions.ResourceConflictException;
import edu.yachay.backend.identity.domain.exceptions.ResourceNotFoundException;
import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestionar escuelas.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SchoolService implements SchoolServicePort {

    private final SchoolRepository schoolRepository;
    private final IdentityMapper mapper;

    @Override
    public SchoolDTO createSchool(SchoolRequest request) {
        if (request.getCode() != null && schoolRepository.existsByCode(request.getCode())) {
            throw new ResourceConflictException("El código de escuela '" + request.getCode() + "' ya existe");
        }

        School school = School.builder()
                .name(request.getName())
                .code(request.getCode())
                .address(request.getAddress())
                .phone(request.getPhone())
                .logoUrl(request.getLogoUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        School savedSchool = schoolRepository.save(school);
        return mapper.toSchoolDTO(savedSchool);
    }

    @Override
    public SchoolDTO updateSchool(Integer schoolId, SchoolRequest request) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Escuela con ID " + schoolId + " no encontrada"));

        if (request.getCode() != null && !request.getCode().equals(school.getCode())) {
            if (schoolRepository.existsByCode(request.getCode())) {
                throw new ResourceConflictException("El código de escuela '" + request.getCode() + "' ya existe");
            }
            school.setCode(request.getCode());
        }

        school.setName(request.getName());
        school.setAddress(request.getAddress());
        school.setPhone(request.getPhone());
        school.setLogoUrl(request.getLogoUrl());
        if (request.getIsActive() != null) school.setIsActive(request.getIsActive());

        School updatedSchool = schoolRepository.save(school);
        return mapper.toSchoolDTO(updatedSchool);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SchoolDTO> getSchoolById(Integer schoolId) {
        return schoolRepository.findById(schoolId).map(mapper::toSchoolDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SchoolDTO> getSchoolByCode(String code) {
        return schoolRepository.findByCode(code).map(mapper::toSchoolDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolDTO> getAllSchools() {
        return schoolRepository.findAll().stream()
                .map(mapper::toSchoolDTO)
                .toList();
    }

    @Override
    public void deleteSchool(Integer schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("Escuela con ID " + schoolId + " no encontrada");
        }
        schoolRepository.deleteById(schoolId);
    }
}
