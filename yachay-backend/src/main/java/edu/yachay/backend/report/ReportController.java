package edu.yachay.backend.report;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reportes")
public class ReportController {

    private static final MediaType XLSX_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final ExcelReportService excelReportService;

    public ReportController(ExcelReportService excelReportService) {
        this.excelReportService = excelReportService;
    }

    @GetMapping("/alumnos.xlsx")
    public ResponseEntity<byte[]> studentsReport() {
        return xlsx("alumnos.xlsx", excelReportService.buildStudentsReport());
    }

    @GetMapping("/docentes.xlsx")
    public ResponseEntity<byte[]> teachersReport() {
        return xlsx("docentes.xlsx", excelReportService.buildTeachersReport());
    }

    @GetMapping("/usuarios.xlsx")
    public ResponseEntity<byte[]> usersReport() {
        return xlsx("usuarios.xlsx", excelReportService.buildUsersReport());
    }

    @GetMapping("/cursos.xlsx")
    public ResponseEntity<byte[]> coursesReport() {
        return xlsx("cursos.xlsx", excelReportService.buildCoursesReport());
    }

    @GetMapping("/postulaciones.xlsx")
    public ResponseEntity<byte[]> admissionsReport() {
        return xlsx("postulaciones.xlsx", excelReportService.buildAdmissionsReport());
    }

    @GetMapping("/notas.xlsx")
    public ResponseEntity<byte[]> gradesReport() {
        return xlsx("notas.xlsx", excelReportService.buildGradesReport());
    }

    private ResponseEntity<byte[]> xlsx(String filename, byte[] content) {
        return ResponseEntity.ok()
                .contentType(XLSX_MEDIA_TYPE)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString()
                )
                .body(content);
    }
}
