package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.AcademicTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicTaskRepository extends JpaRepository<AcademicTask, Integer> {
    boolean existsByCourse_IdAndTitle(Integer courseId, String title);
}
