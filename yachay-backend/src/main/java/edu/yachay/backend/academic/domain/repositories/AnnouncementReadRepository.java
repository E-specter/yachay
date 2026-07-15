package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, Integer> {
    Optional<AnnouncementRead> findByAnnouncement_IdAndStudent_Id(Integer announcementId, Integer studentId);
    List<AnnouncementRead> findByStudent_Id(Integer studentId);
}
