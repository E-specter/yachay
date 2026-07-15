package edu.yachay.backend.portal;

import edu.yachay.backend.academic.domain.models.*;
import edu.yachay.backend.academic.domain.repositories.*;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import edu.yachay.backend.identity.domain.repositories.TeacherProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortalServiceTest {
    @Mock TeacherProfileRepository teachers;
    @Mock StudentProfileRepository students;
    @Mock CourseRepository courses;
    @Mock EnrollmentRepository enrollments;
    @Mock AcademicTaskRepository tasks;
    @Mock GradeRecordRepository grades;
    @Mock AnnouncementRepository announcements;
    @Mock HomeworkSubmissionRepository submissions;
    @Mock AnnouncementReadRepository reads;
    private PortalService service;

    @BeforeEach
    void setUp() {
        service = new PortalService(teachers, students, courses, enrollments, tasks, grades, announcements, submissions, reads);
    }

    @Test
    void teacherCannotChangeAnotherTeachersTask() {
        User user = User.builder().id(10L).build();
        TeacherProfile current = TeacherProfile.builder().id(1).build();
        AcademicTask foreignTask = AcademicTask.builder().id(7).teacher(TeacherProfile.builder().id(2).build()).build();
        when(teachers.findByProfile_User_Id(10L)).thenReturn(Optional.of(current));
        when(tasks.findById(7)).thenReturn(Optional.of(foreignTask));

        assertThatThrownBy(() -> service.updateTeacherTaskStatus(user, 7, new PortalDtos.StatusRequest("CERRADA")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        verify(tasks, never()).save(any());
    }

    @Test
    void studentCannotSubmitTaskOutsideEnrollment() {
        User user = User.builder().id(20L).build();
        StudentProfile student = StudentProfile.builder().id(4).build();
        Course course = Course.builder().id(9).build();
        AcademicTask task = AcademicTask.builder().id(11).course(course).dueAt(LocalDateTime.now().plusDays(1)).build();
        when(students.findByProfile_User_Id(20L)).thenReturn(Optional.of(student));
        when(tasks.findById(11)).thenReturn(Optional.of(task));
        when(enrollments.existsByCourse_IdAndStudent_Id(9, 4)).thenReturn(false);

        assertThatThrownBy(() -> service.submitHomework(user, 11, new PortalDtos.SubmissionRequest("Respuesta", null)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        verify(submissions, never()).save(any());
    }
}
