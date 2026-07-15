package edu.yachay.backend.academic.domain.repositories;

import edu.yachay.backend.academic.domain.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);

    List<Course> findByTeacher_IdOrderByNameAsc(Integer teacherId);
}
