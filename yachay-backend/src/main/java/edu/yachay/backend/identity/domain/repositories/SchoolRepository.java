package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a entidades School en la base de datos.
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findByCode(String code);

    boolean existsByCode(String code);
}
