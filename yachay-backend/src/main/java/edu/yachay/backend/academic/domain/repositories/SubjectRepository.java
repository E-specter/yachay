package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Subject> findByCode(String code);

    Optional<Subject> findByName(String name);

    boolean existsByCode(String code);
}
