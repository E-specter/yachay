package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.GradeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRecordRepository extends JpaRepository<GradeRecord, Integer> {
    boolean existsByCourse_IdAndStudent_IdAndBimester(Integer courseId, Integer studentId, String bimester);
}
