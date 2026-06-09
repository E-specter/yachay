package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
