package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.SchoolSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolSectionRepository extends JpaRepository<SchoolSection, Integer> {
    boolean existsByAcademicYear_IdAndGradeAndName(Integer academicYearId, String grade, String name);
}
