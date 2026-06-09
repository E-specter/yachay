package edu.yachay.backend.report;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.models.TeacherProfile;
import edu.yachay.backend.identity.domain.models.User;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;
import edu.yachay.backend.identity.domain.repositories.TeacherProfileRepository;
import edu.yachay.backend.identity.domain.repositories.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExcelReportService {

    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserRepository userRepository;
    private final AdmissionApplicationRepository admissionApplicationRepository;

    public ExcelReportService(
            StudentProfileRepository studentProfileRepository,
            TeacherProfileRepository teacherProfileRepository,
            UserRepository userRepository,
            AdmissionApplicationRepository admissionApplicationRepository
    ) {
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.userRepository = userRepository;
        this.admissionApplicationRepository = admissionApplicationRepository;
    }

    @Transactional(readOnly = true)
    public byte[] buildStudentsReport() {
        List<StudentProfile> students = studentProfileRepository.findAll();

        return createWorkbook("Alumnos", List.of(
                "ID", "Codigo", "Alumno", "Correo", "Colegio", "Grado", "Seccion", "Matricula", "Estado"
        ), (sheet, headerStyle) -> {
            writeHeader(sheet, headerStyle, List.of(
                    "ID", "Codigo", "Alumno", "Correo", "Colegio", "Grado", "Seccion", "Matricula", "Estado"
            ));

            int rowIndex = 1;
            for (StudentProfile student : students) {
                Profile profile = student.getProfile();
                User user = profile != null ? profile.getUser() : null;
                Row row = sheet.createRow(rowIndex++);
                writeCells(row,
                        student.getId(),
                        value(student.getStudentCode()),
                        fullName(profile),
                        user != null ? value(user.getEmail()) : "",
                        student.getSchool() != null ? value(student.getSchool().getName()) : "",
                        student.getGradeLevel(),
                        value(student.getSection()),
                        student.getEnrollmentDate(),
                        activeText(profile)
                );
            }
        });
    }

    @Transactional(readOnly = true)
    public byte[] buildTeachersReport() {
        List<TeacherProfile> teachers = teacherProfileRepository.findAll();

        return createWorkbook("Docentes", List.of(
                "ID", "Empleado", "Docente", "Correo", "Colegio", "Especialidad", "Contratacion", "Estado"
        ), (sheet, headerStyle) -> {
            writeHeader(sheet, headerStyle, List.of(
                    "ID", "Empleado", "Docente", "Correo", "Colegio", "Especialidad", "Contratacion", "Estado"
            ));

            int rowIndex = 1;
            for (TeacherProfile teacher : teachers) {
                Profile profile = teacher.getProfile();
                User user = profile != null ? profile.getUser() : null;
                Row row = sheet.createRow(rowIndex++);
                writeCells(row,
                        teacher.getId(),
                        value(teacher.getEmployeeId()),
                        fullName(profile),
                        user != null ? value(user.getEmail()) : "",
                        teacher.getSchool() != null ? value(teacher.getSchool().getName()) : "",
                        value(teacher.getSpecialization()),
                        teacher.getHireDate(),
                        activeText(profile)
                );
            }
        });
    }

    @Transactional(readOnly = true)
    public byte[] buildUsersReport() {
        List<User> users = userRepository.findAll();

        return createWorkbook("Usuarios", List.of(
                "ID", "Correo", "Nombre visible", "Roles", "Confirmado", "Ultimo ingreso", "Creacion"
        ), (sheet, headerStyle) -> {
            writeHeader(sheet, headerStyle, List.of(
                    "ID", "Correo", "Nombre visible", "Roles", "Confirmado", "Ultimo ingreso", "Creacion"
            ));

            int rowIndex = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIndex++);
                writeCells(row,
                        user.getId(),
                        value(user.getEmail()),
                        value(user.getDisplayName()),
                        roleNames(user.getRoles()),
                        user.getEmailConfirmedAt() != null ? "Si" : "No",
                        user.getLastSignInAt(),
                        user.getCreatedAt()
                );
            }
        });
    }

    public byte[] buildCoursesReport() {
        return buildPreparedReport(
                "Cursos",
                "El backend actual aun no contiene entidad/repositorio de cursos. El endpoint queda preparado para conectar el modulo academico."
        );
    }

    @Transactional(readOnly = true)
    public byte[] buildAdmissionsReport() {
        List<AdmissionApplication> applications = admissionApplicationRepository.findAll();

        return createWorkbook("Postulaciones", List.of(
                "ID", "Postulante", "Apoderado", "Telefono", "Correo", "Nivel", "Grado", "Estado", "Observaciones", "Fecha Registro"
        ), (sheet, headerStyle) -> {
            writeHeader(sheet, headerStyle, List.of(
                    "ID", "Postulante", "Apoderado", "Telefono", "Correo", "Nivel", "Grado", "Estado", "Observaciones", "Fecha Registro"
            ));

            int rowIndex = 1;
            for (AdmissionApplication application : applications) {
                Row row = sheet.createRow(rowIndex++);
                writeCells(row,
                        application.getId(),
                        application.studentFullName(),
                        application.guardianFullName(),
                        application.getGuardianPhone(),
                        application.getGuardianEmail(),
                        application.getLevel(),
                        application.getGrade(),
                        application.getStatus(),
                        application.getObservations(),
                        application.getCreatedAt()
                );
            }
        });
    }

    public byte[] buildGradesReport() {
        return buildPreparedReport(
                "Notas",
                "El backend actual aun no contiene entidad/repositorio de notas. El endpoint queda preparado para calificaciones."
        );
    }

    private byte[] buildPreparedReport(String sheetName, String detail) {
        return createWorkbook(sheetName, List.of("Modulo", "Estado", "Detalle"), (sheet, headerStyle) -> {
            writeHeader(sheet, headerStyle, List.of("Modulo", "Estado", "Detalle"));
            Row row = sheet.createRow(1);
            writeCells(row, sheetName, "Preparado", detail);
        });
    }

    private byte[] createWorkbook(String sheetName, List<String> columns, SheetWriter writer) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            CellStyle headerStyle = headerStyle(workbook);
            writer.write(sheet, headerStyle);

            for (int index = 0; index < columns.size(); index++) {
                sheet.autoSizeColumn(index);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo generar el reporte XLSX.", exception);
        }
    }

    private void writeHeader(Sheet sheet, CellStyle headerStyle, List<String> headers) {
        Row row = sheet.createRow(0);
        for (int index = 0; index < headers.size(); index++) {
            Cell cell = row.createCell(index);
            cell.setCellValue(headers.get(index));
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeCells(Row row, Object... values) {
        for (int index = 0; index < values.length; index++) {
            row.createCell(index).setCellValue(value(values[index]));
        }
    }

    private CellStyle headerStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private String fullName(Profile profile) {
        return profile != null ? value(profile.getFullName()) : "";
    }

    private String activeText(Profile profile) {
        return profile != null && Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVO" : "INACTIVO";
    }

    private String roleNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return "";
        }

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
    }

    private String value(Object value) {
        return value != null ? String.valueOf(value) : "";
    }

    @FunctionalInterface
    private interface SheetWriter {
        void write(Sheet sheet, CellStyle headerStyle);
    }
}
