package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer> {
    Optional<AcademicYear> findByYear(Integer year);

    boolean existsByYear(Integer year);
}
