package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a entidades StudentProfile en la base de datos.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Integer> {
    Optional<StudentProfile> findByStudentCode(String studentCode);

    Optional<StudentProfile> findByProfileId(Integer profileId);

    boolean existsByStudentCode(String studentCode);
}
