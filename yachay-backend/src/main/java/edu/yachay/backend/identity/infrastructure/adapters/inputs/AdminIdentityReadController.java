package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.StudentProfileDTO;
import edu.yachay.backend.identity.application.dtos.TeacherProfileDTO;
import edu.yachay.backend.identity.application.dtos.UserDTO;
import edu.yachay.backend.identity.application.ports.inputs.StudentProfileServicePort;
import edu.yachay.backend.identity.application.ports.inputs.TeacherProfileServicePort;
import edu.yachay.backend.identity.application.ports.inputs.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminIdentityReadController {

    private final StudentProfileServicePort studentProfileService;
    private final TeacherProfileServicePort teacherProfileService;
    private final UserServicePort userService;

    @GetMapping("/alumnos")
    public ResponseEntity<List<StudentProfileDTO>> getStudents() {
        return ResponseEntity.ok(studentProfileService.getAllStudentProfiles());
    }

    @GetMapping("/docentes")
    public ResponseEntity<List<TeacherProfileDTO>> getTeachers() {
        return ResponseEntity.ok(teacherProfileService.getAllTeacherProfiles());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
