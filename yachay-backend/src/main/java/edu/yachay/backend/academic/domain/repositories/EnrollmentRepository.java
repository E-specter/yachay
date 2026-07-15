package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByCourse_IdAndStudent_Id(Integer courseId, Integer studentId);

    List<Enrollment> findByStudent_Id(Integer studentId);

    List<Enrollment> findByCourse_Id(Integer courseId);

    List<Enrollment> findByCourse_Teacher_Id(Integer teacherId);
}
