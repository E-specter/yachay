package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.GuardianProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a entidades GuardianProfile en la base de datos.
 */
@Repository
public interface GuardianProfileRepository extends JpaRepository<GuardianProfile, Integer> {
    Optional<GuardianProfile> findByProfileId(Integer profileId);
}
