package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.SchoolDTO;
import edu.yachay.backend.identity.application.dtos.SchoolRequest;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con escuelas.
 */
public interface SchoolServicePort {
    SchoolDTO createSchool(SchoolRequest request);

    SchoolDTO updateSchool(Integer schoolId, SchoolRequest request);

    Optional<SchoolDTO> getSchoolById(Integer schoolId);

    Optional<SchoolDTO> getSchoolByCode(String code);

    List<SchoolDTO> getAllSchools();

    void deleteSchool(Integer schoolId);
}
