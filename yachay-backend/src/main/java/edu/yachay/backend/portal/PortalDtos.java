package edu.yachay.backend.portal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class PortalDtos {
    private PortalDtos() {
    }

    public record Metric(String label, String value) {
    }

    public record TeacherMetric(String label, long value) {
    }

    public record TeacherCourse(Integer id, String codigo, String nombre, String nivel, String grado,
                                String seccion, int cantidadAlumnos, String horario, String estado) {
    }

    public record TeacherStudent(Integer id, String codigo, String nombres, String apellidos,
                                 String documento, String nivel, String grado, String seccion,
                                 String curso, BigDecimal promedio, String estado) {
    }

    public record TeacherTask(Integer id, String titulo, String descripcion, String curso, Integer cursoId,
                              String nivel, String grado, String seccion, String fechaPublicacion,
                              String fechaEntrega, String estado, long entregas, long pendientes) {
    }

    public record TeacherGrade(Integer id, Integer alumnoId, Integer cursoId, String alumno, String curso,
                               String bimestre, BigDecimal nota, String observacion,
                               String fechaRegistro, String estado) {
    }

    public record TeacherAnnouncement(Integer id, Integer cursoId, String titulo, String contenido,
                                      String curso, String nivel, String grado, String seccion,
                                      String fechaPublicacion, String estado) {
    }

    public record TeacherProfileDto(String codigo, String nombres, String apellidos, String documento,
                                    String correoInstitucional, String especialidad, String telefono,
                                    String fechaContratacion, String institucion) {
    }

    public record TeacherReviewTask(Integer id, String titulo, String curso, String aula,
                                    String fechaEntrega, long pendientes) {
    }

    public record TeacherDashboardAnnouncement(Integer id, String titulo, String fechaPublicacion,
                                                String estado) {
    }

    public record TeacherAssignedCourse(Integer id, String codigo, String nombre, String aula,
                                        int cantidadAlumnos) {
    }

    public record TeacherDashboard(List<TeacherMetric> metrics, List<TeacherReviewTask> reviewTasks,
                                   List<TeacherDashboardAnnouncement> announcements,
                                   List<TeacherAssignedCourse> courses) {
    }

    public record StudentCourse(Integer id, String codigo, String nombre, String docente, String nivel,
                                String grado, String seccion, BigDecimal promedio, String estado) {
    }

    public record StudentCourseDetail(Integer id, String nombre, String docente, String aula,
                                      String progreso, String proximaActividad, String descripcion,
                                      List<String> horario, List<String> tareas, List<String> notas,
                                      List<String> comunicados) {
    }

    public record StudentTask(Integer id, String titulo, String descripcion, String curso, String docente,
                              String fechaPublicacion, String fechaEntrega, String estadoEntrega) {
    }

    public record StudentTaskDetail(Integer id, String titulo, String curso, String docente,
                                    String fechaPublicacion, String fechaEntrega, String estado,
                                    String descripcion, String instrucciones, String recurso,
                                    String contenidoEntrega, String fechaEntregaReal) {
    }

    public record StudentGrade(Integer id, String curso, String docente, String bimestre,
                               BigDecimal nota, String observacion, String fechaRegistro) {
    }

    public record StudentAnnouncement(Integer id, String titulo, String contenido, String remitente,
                                      String fechaPublicacion, boolean leido) {
    }

    public record StudentProfileDto(String codigo, String nombres, String apellidos, String documento,
                                    String correoInstitucional, String nivel, String grado, String seccion,
                                    String apoderado, String correoApoderado, String celularApoderado,
                                    String institucion) {
    }

    public record StudentUpcomingTask(Integer id, String titulo, String curso, String fechaEntrega,
                                      String estadoEntrega) {
    }

    public record StudentRecentGrade(Integer id, String curso, String bimestre, BigDecimal nota,
                                     String fechaRegistro) {
    }

    public record StudentRecentAnnouncement(Integer id, String titulo, String remitente,
                                             String fechaPublicacion) {
    }

    public record StudentDashboard(List<Metric> metrics, List<StudentUpcomingTask> upcomingTasks,
                                   List<StudentRecentGrade> recentGrades,
                                   List<StudentRecentAnnouncement> announcements) {
    }

    public record TaskRequest(@NotNull Integer cursoId, @NotBlank String titulo, String descripcion,
                              LocalDateTime fechaPublicacion, @NotNull LocalDateTime fechaEntrega,
                              @DecimalMin("0") BigDecimal puntajeMaximo, String tipo,
                              Boolean permitirEntregaTardia, String estado) {
    }

    public record GradeRequest(@NotNull Integer cursoId, @NotNull Integer alumnoId,
                               @NotBlank String bimestre,
                               @NotNull @DecimalMin("0") @DecimalMax("20") BigDecimal nota,
                               String tipoEvaluacion, String observacion, String estado) {
    }

    public record AnnouncementRequest(Integer cursoId, @NotBlank String titulo,
                                      @NotBlank String contenido, String destinatario,
                                      LocalDateTime fechaPublicacion, LocalDateTime fechaExpiracion,
                                      Boolean fijado, String estado) {
    }

    public record StatusRequest(@NotBlank String estado) {
    }

    public record SubmissionRequest(@NotBlank String contenido, String archivoUrl) {
    }

    public record SubmissionResponse(Integer id, Integer tareaId, Integer alumnoId, String alumno,
                                     String contenido, String archivoUrl, String estado,
                                     BigDecimal nota, String retroalimentacion, String fechaEntrega) {
    }
}
