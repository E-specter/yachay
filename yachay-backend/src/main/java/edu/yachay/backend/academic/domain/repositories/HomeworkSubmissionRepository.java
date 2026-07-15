package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.HomeworkSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission, Integer> {
    Optional<HomeworkSubmission> findByTask_IdAndStudent_Id(Integer taskId, Integer studentId);
    List<HomeworkSubmission> findByTask_Id(Integer taskId);
    List<HomeworkSubmission> findByStudent_Id(Integer studentId);
    long countByTask_Id(Integer taskId);
}
