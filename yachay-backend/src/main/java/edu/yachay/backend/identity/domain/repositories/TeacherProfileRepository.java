package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a entidades TeacherProfile en la base de datos.
 */
@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Integer> {
    Optional<TeacherProfile> findByEmployeeId(String employeeId);

    Optional<TeacherProfile> findByProfileId(Integer profileId);

    Optional<TeacherProfile> findByProfile_User_Id(Long userId);

    boolean existsByEmployeeId(String employeeId);
}
