package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    boolean existsByTitleAndSchool_Code(String title, String schoolCode);

    List<Announcement> findByAuthor_IdOrderByPublishedAtDesc(Long authorId);
    List<Announcement> findBySchool_IdOrderByPublishedAtDesc(Integer schoolId);
}
