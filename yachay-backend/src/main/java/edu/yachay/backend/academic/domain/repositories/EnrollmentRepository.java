package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByCourse_IdAndStudent_Id(Integer courseId, Integer studentId);
}
