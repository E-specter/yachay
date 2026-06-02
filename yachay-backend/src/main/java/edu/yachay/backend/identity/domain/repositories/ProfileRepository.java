package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para acceder a entidades Profile en la base de datos.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
