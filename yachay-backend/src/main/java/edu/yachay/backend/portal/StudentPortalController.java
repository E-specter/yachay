package edu.yachay.backend.portal;

import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.portal.PortalDtos.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alumno")
public class StudentPortalController {
    private final PortalService portalService;

    public StudentPortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/dashboard")
    public StudentDashboard dashboard(@AuthenticationPrincipal User user) {
        return portalService.studentDashboard(user);
    }

    @GetMapping("/cursos")
    public List<StudentCourse> courses(@AuthenticationPrincipal User user) {
        return portalService.studentCourses(user);
    }

    @GetMapping("/cursos/{id}")
    public StudentCourseDetail course(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return portalService.studentCourseDetail(user, id);
    }

    @GetMapping("/tareas")
    public List<StudentTask> tasks(@AuthenticationPrincipal User user) {
        return portalService.studentTasks(user);
    }

    @GetMapping("/tareas/{id}")
    public StudentTaskDetail task(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return portalService.studentTaskDetail(user, id);
    }

    @PostMapping("/tareas/{id}/entrega")
    public SubmissionResponse submitTask(@AuthenticationPrincipal User user, @PathVariable Integer id,
                                         @Valid @RequestBody SubmissionRequest request) {
        return portalService.submitHomework(user, id, request);
    }

    @GetMapping("/notas")
    public List<StudentGrade> grades(@AuthenticationPrincipal User user) {
        return portalService.studentGrades(user);
    }

    @GetMapping("/comunicados")
    public List<StudentAnnouncement> announcements(@AuthenticationPrincipal User user) {
        return portalService.studentAnnouncements(user);
    }

    @GetMapping("/comunicados/{id}")
    public StudentAnnouncement announcement(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return portalService.studentAnnouncement(user, id);
    }

    @PatchMapping("/comunicados/{id}/leido")
    public StudentAnnouncement markRead(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return portalService.markAnnouncementRead(user, id);
    }

    @GetMapping("/perfil")
    public StudentProfileDto profile(@AuthenticationPrincipal User user) {
        return portalService.studentProfile(user);
    }
}
