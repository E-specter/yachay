package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repositorio para acceder a entidades Role en la base de datos.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);

    Set<Role> findByNameIn(Set<String> names);

    boolean existsByName(String name);
}
