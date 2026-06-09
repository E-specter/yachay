package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    boolean existsByTitleAndSchool_Code(String title, String schoolCode);
}
