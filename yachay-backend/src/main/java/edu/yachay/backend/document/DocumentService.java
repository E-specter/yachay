package edu.yachay.backend.document;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.document.dto.DocumentResponse;
import edu.yachay.backend.identity.domain.models.*;
import edu.yachay.backend.identity.domain.repositories.StudentProfileRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DocumentService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IlovePdfClient ilovePdfClient;
    private final StudentProfileRepository studentProfileRepository;
    private final AdmissionApplicationRepository admissionApplicationRepository;

    public DocumentService(
            IlovePdfClient ilovePdfClient,
            StudentProfileRepository studentProfileRepository,
            AdmissionApplicationRepository admissionApplicationRepository
    ) {
        this.ilovePdfClient = ilovePdfClient;
        this.studentProfileRepository = studentProfileRepository;
        this.admissionApplicationRepository = admissionApplicationRepository;
    }

    public DocumentResponse generateAdmissionPdf(Long admissionId) {
        return response("postulacion", admissionId);
    }

    public DocumentResponse generateStudentPdf(Long studentId) {
        return response("alumno", studentId);
    }

    @Transactional(readOnly = true)
    public byte[] buildStudentPdf(Integer studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado."));

        Profile profile = student.getProfile();
        User user = profile != null ? profile.getUser() : null;
        School school = student.getSchool();

        return createPdf("Yachay - Ficha del Alumno", table -> {
            addRow(table, "ID alumno", student.getId());
            addRow(table, "Nombres", profile != null ? profile.getFirstName() : "");
            addRow(table, "Apellidos", profile != null ? profile.getLastName() : "");
            addRow(table, "Correo institucional", user != null ? user.getEmail() : "");
            addRow(table, "Grado", student.getGradeLevel() + " Primaria");
            addRow(table, "Sección", student.getSection());
            addRow(table, "Código de estudiante", student.getStudentCode());
            addRow(table, "Estado", profile == null || Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVO" : "INACTIVO");
            addRow(table, "Fecha de matrícula", formatDate(student.getEnrollmentDate()));
            addRow(table, "Colegio", school != null ? school.getName() : "Colegio Manuel Gonzales Prada");
        });
    }

    @Transactional(readOnly = true)
    public byte[] buildAdmissionPdf(Long admissionId) {
        AdmissionApplication application = admissionApplicationRepository.findById(admissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulación no encontrada."));

        return createPdf("Yachay - Ficha de Postulación", table -> {
            addRow(table, "ID postulación", application.getId());
            addRow(table, "Postulante", application.studentFullName());
            addRow(table, "Apoderado", application.guardianFullName());
            addRow(table, "Teléfono del apoderado", application.getGuardianPhone());
            addRow(table, "Correo del apoderado", application.getGuardianEmail());
            addRow(table, "Nivel", application.getLevel());
            addRow(table, "Grado", application.getGrade());
            addRow(table, "Estado", displayStatus(application.getStatus()));
            addRow(table, "Observaciones", application.getObservations());
            addRow(table, "Fecha de registro", formatDateTime(application.getCreatedAt()));
        });
    }

    private DocumentResponse response(String documentType, Long entityId) {
        boolean configured = ilovePdfClient.isConfigured();
        String message = ilovePdfClient.prepareGeneration(documentType, entityId);

        return new DocumentResponse(configured, message, documentType, entityId, configured);
    }

    private byte[] createPdf(String title, PdfTableWriter tableWriter) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 48, 48, 48, 48);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(13, 13, 13));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(90, 90, 90));

            Paragraph heading = new Paragraph(title, titleFont);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(8);
            document.add(heading);

            Paragraph subtitle = new Paragraph("Colegio Manuel Gonzales Prada", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(24);
            document.add(subtitle);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{32, 68});
            tableWriter.write(table);
            document.add(table);

            Paragraph footer = new Paragraph(
                    "Documento generado por Yachay Campus Virtual\nFecha de generación: " + formatDateTime(LocalDateTime.now()),
                    subtitleFont
            );
            footer.setSpacingBefore(28);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF.", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF.", ex);
        }
    }

    private void addRow(PdfPTable table, String label, Object value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(13, 13, 13));
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(31, 41, 55));

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(9);
        labelCell.setBackgroundColor(new Color(240, 241, 242));
        labelCell.setBorderColor(new Color(210, 220, 230));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? String.valueOf(value) : "", valueFont));
        valueCell.setPadding(9);
        valueCell.setBorderColor(new Color(210, 220, 230));
        table.addCell(valueCell);
    }

    private String displayStatus(String status) {
        if (status == null || status.isBlank()) return "Pendiente";
        return switch (status.trim().toUpperCase()) {
            case "ACEPTADA" -> "Aceptada";
            case "RECHAZADA" -> "Rechazada";
            default -> "Pendiente";
        };
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "";
    }

    @FunctionalInterface
    private interface PdfTableWriter {
        void write(PdfPTable table);
    }
}
