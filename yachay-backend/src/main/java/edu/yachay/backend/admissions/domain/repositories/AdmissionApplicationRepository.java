package edu.yachay.backend.admissions.domain.repositories;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    List<AdmissionApplication> findByStatus(String status);

    boolean existsByStudentFirstNameAndStudentLastNameAndGuardianEmail(
            String studentFirstName,
            String studentLastName,
            String guardianEmail
    );
}
