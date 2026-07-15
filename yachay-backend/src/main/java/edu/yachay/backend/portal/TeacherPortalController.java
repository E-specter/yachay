package edu.yachay.backend.portal;

import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.portal.PortalDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docente")
public class TeacherPortalController {
    private final PortalService portalService;

    public TeacherPortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/dashboard")
    public TeacherDashboard dashboard(@AuthenticationPrincipal User user) {
        return portalService.teacherDashboard(user);
    }

    @GetMapping("/cursos")
    public List<TeacherCourse> courses(@AuthenticationPrincipal User user) {
        return portalService.teacherCourses(user);
    }

    @GetMapping("/alumnos")
    public List<TeacherStudent> students(@AuthenticationPrincipal User user) {
        return portalService.teacherStudents(user);
    }

    @GetMapping("/tareas")
    public List<TeacherTask> tasks(@AuthenticationPrincipal User user) {
        return portalService.teacherTasks(user);
    }

    @PostMapping("/tareas")
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherTask createTask(@AuthenticationPrincipal User user, @Valid @RequestBody TaskRequest request) {
        return portalService.createTeacherTask(user, request);
    }

    @PutMapping("/tareas/{id}")
    public TeacherTask updateTask(@AuthenticationPrincipal User user, @PathVariable Integer id,
                                  @Valid @RequestBody TaskRequest request) {
        return portalService.updateTeacherTask(user, id, request);
    }

    @PatchMapping("/tareas/{id}/estado")
    public TeacherTask updateTaskStatus(@AuthenticationPrincipal User user, @PathVariable Integer id,
                                        @Valid @RequestBody StatusRequest request) {
        return portalService.updateTeacherTaskStatus(user, id, request);
    }

    @GetMapping("/tareas/{id}/entregas")
    public List<SubmissionResponse> taskSubmissions(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return portalService.taskSubmissions(user, id);
    }

    @GetMapping("/notas")
    public List<TeacherGrade> grades(@AuthenticationPrincipal User user) {
        return portalService.teacherGrades(user);
    }

    @PostMapping("/notas")
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherGrade createGrade(@AuthenticationPrincipal User user, @Valid @RequestBody GradeRequest request) {
        return portalService.createTeacherGrade(user, request);
    }

    @PutMapping("/notas/{id}")
    public TeacherGrade updateGrade(@AuthenticationPrincipal User user, @PathVariable Integer id,
                                    @Valid @RequestBody GradeRequest request) {
        return portalService.updateTeacherGrade(user, id, request);
    }

    @PatchMapping("/notas/{id}/estado")
    public TeacherGrade updateGradeStatus(@AuthenticationPrincipal User user, @PathVariable Integer id,
                                          @Valid @RequestBody StatusRequest request) {
        return portalService.updateTeacherGradeStatus(user, id, request);
    }

    @GetMapping("/comunicados")
    public List<TeacherAnnouncement> announcements(@AuthenticationPrincipal User user) {
        return portalService.teacherAnnouncements(user);
    }

    @PostMapping("/comunicados")
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherAnnouncement createAnnouncement(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody AnnouncementRequest request) {
        return portalService.createTeacherAnnouncement(user, request);
    }

    @PutMapping("/comunicados/{id}")
    public TeacherAnnouncement updateAnnouncement(@AuthenticationPrincipal User user, @PathVariable Integer id,
                                                  @Valid @RequestBody AnnouncementRequest request) {
        return portalService.updateTeacherAnnouncement(user, id, request);
    }

    @PatchMapping("/comunicados/{id}/estado")
    public TeacherAnnouncement updateAnnouncementStatus(@AuthenticationPrincipal User user,
                                                        @PathVariable Integer id,
                                                        @Valid @RequestBody StatusRequest request) {
        return portalService.updateTeacherAnnouncementStatus(user, id, request);
    }

    @GetMapping("/perfil")
    public TeacherProfileDto profile(@AuthenticationPrincipal User user) {
        return portalService.teacherProfile(user);
    }
}
