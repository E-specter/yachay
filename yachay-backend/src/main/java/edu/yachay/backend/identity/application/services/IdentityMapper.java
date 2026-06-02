package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.domain.models.*;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio y DTOs.
 */
@Component
public class IdentityMapper {

    public UserDTO toUserDTO(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .emailConfirmed(user.getEmailConfirmedAt() != null)
                .phoneConfirmed(user.getPhoneConfirmedAt() != null)
                .lastSignInAt(user.getLastSignInAt())
                .roleNames(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(java.util.stream.Collectors.toSet()))
                .profile(toProfileDTO(user.getProfile()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public ProfileDTO toProfileDTO(Profile profile) {
        if (profile == null) return null;

        return ProfileDTO.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .fullName(profile.getFullName())
                .dateOfBirth(profile.getDateOfBirth())
                .avatarUrl(profile.getAvatarUrl())
                .isActive(profile.getIsActive())
                .studentProfile(toStudentProfileDTO(profile.getStudentProfile()))
                .teacherProfile(toTeacherProfileDTO(profile.getTeacherProfile()))
                .guardianProfile(toGuardianProfileDTO(profile.getGuardianProfile()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public RoleDTO toRoleDTO(Role role) {
        if (role == null) return null;

        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public StudentProfileDTO toStudentProfileDTO(StudentProfile studentProfile) {
        if (studentProfile == null) return null;

        return StudentProfileDTO.builder()
                .id(studentProfile.getId())
                .studentCode(studentProfile.getStudentCode())
                .gradeLevel(studentProfile.getGradeLevel())
                .section(studentProfile.getSection())
                .enrollmentDate(studentProfile.getEnrollmentDate())
                .schoolId(studentProfile.getSchool().getId())
                .schoolName(studentProfile.getSchool().getName())
                .createdAt(studentProfile.getCreatedAt())
                .updatedAt(studentProfile.getUpdatedAt())
                .build();
    }

    public TeacherProfileDTO toTeacherProfileDTO(TeacherProfile teacherProfile) {
        if (teacherProfile == null) return null;

        return TeacherProfileDTO.builder()
                .id(teacherProfile.getId())
                .employeeId(teacherProfile.getEmployeeId())
                .specialization(teacherProfile.getSpecialization())
                .hireDate(teacherProfile.getHireDate())
                .schoolId(teacherProfile.getSchool().getId())
                .schoolName(teacherProfile.getSchool().getName())
                .createdAt(teacherProfile.getCreatedAt())
                .updatedAt(teacherProfile.getUpdatedAt())
                .build();
    }

    public GuardianProfileDTO toGuardianProfileDTO(GuardianProfile guardianProfile) {
        if (guardianProfile == null) return null;

        return GuardianProfileDTO.builder()
                .id(guardianProfile.getId())
                .relationship(guardianProfile.getRelationship())
                .occupation(guardianProfile.getOccupation())
                .studentIds(guardianProfile.getStudents().stream()
                        .map(StudentProfile::getId)
                        .collect(java.util.stream.Collectors.toSet()))
                .createdAt(guardianProfile.getCreatedAt())
                .updatedAt(guardianProfile.getUpdatedAt())
                .build();
    }

    public SchoolDTO toSchoolDTO(School school) {
        if (school == null) return null;

        return SchoolDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .code(school.getCode())
                .address(school.getAddress())
                .phone(school.getPhone())
                .logoUrl(school.getLogoUrl())
                .isActive(school.getIsActive())
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }
}
