package edu.yachay.backend.document;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.yachay.backend.admissions.domain.models.AdmissionApplication;
import edu.yachay.backend.admissions.domain.repositories.AdmissionApplicationRepository;
import edu.yachay.backend.document.dto.DocumentResponse;
import edu.yachay.backend.identity.domain.models.Profile;
import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.models.StudentProfile;
import edu.yachay.backend.identity.domain.models.User;
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

    private static final Color INK = new Color(13, 13, 13);
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color MUTED = new Color(92, 102, 115);
    private static final Color LINE = new Color(214, 222, 230);
    private static final Color SOFT = new Color(240, 241, 242);
    private static final Color AQUA = new Color(119, 242, 242);

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

        return createPdf("Ficha del Alumno", document -> {
            addSection(document, "Datos personales", table -> {
                addRow(table, "ID alumno", student.getId());
                addRow(table, "Nombres", profile != null ? profile.getFirstName() : "");
                addRow(table, "Apellidos", profile != null ? profile.getLastName() : "");
                addRow(table, "Correo institucional", user != null ? user.getEmail() : "");
            });

            addSection(document, "Datos academicos", table -> {
                addRow(table, "Colegio", school != null ? school.getName() : "Colegio Manuel Gonzales Prada");
                addRow(table, "Codigo de estudiante", student.getStudentCode());
                addRow(table, "Grado", student.getGradeLevel() + " Primaria");
                addRow(table, "Seccion", student.getSection());
                addRow(table, "Fecha de matricula", formatDate(student.getEnrollmentDate()));
            });

            addSection(document, "Estado del registro", table -> {
                addRow(table, "Estado", profile == null || Boolean.TRUE.equals(profile.getIsActive()) ? "ACTIVO" : "INACTIVO");
                addRow(table, "Fecha de generacion", formatDateTime(LocalDateTime.now()));
            });
        });
    }

    @Transactional(readOnly = true)
    public byte[] buildAdmissionPdf(Long admissionId) {
        AdmissionApplication application = admissionApplicationRepository.findById(admissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada."));

        return createPdf("Ficha de Postulacion", document -> {
            addSection(document, "Datos del postulante", table -> {
                addRow(table, "ID postulacion", application.getId());
                addRow(table, "Postulante", application.studentFullName());
                addRow(table, "Nivel", application.getLevel());
                addRow(table, "Grado", application.getGrade());
            });

            addSection(document, "Datos del apoderado", table -> {
                addRow(table, "Apoderado", application.guardianFullName());
                addRow(table, "Telefono", application.getGuardianPhone());
                addRow(table, "Correo", application.getGuardianEmail());
            });

            addSection(document, "Informacion de admision", table -> {
                addRow(table, "Estado", displayStatus(application.getStatus()));
                addRow(table, "Observaciones", application.getObservations());
                addRow(table, "Fecha de registro", formatDateTime(application.getCreatedAt()));
                addRow(table, "Fecha de generacion", formatDateTime(LocalDateTime.now()));
            });
        });
    }

    private DocumentResponse response(String documentType, Long entityId) {
        boolean configured = ilovePdfClient.isConfigured();
        String message = ilovePdfClient.prepareGeneration(documentType, entityId);

        return new DocumentResponse(configured, message, documentType, entityId, configured);
    }

    private byte[] createPdf(String subtitle, PdfContentWriter contentWriter) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 48, 48, 42, 42);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addHeader(document, subtitle);
            contentWriter.write(document);
            addFooter(document);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF.", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar el PDF.", ex);
        }
    }

    private void addHeader(Document document, String subtitle) throws DocumentException {
        Paragraph title = new Paragraph("Yachay Campus Virtual", font(20, Font.BOLD, INK));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        document.add(title);

        Paragraph documentType = new Paragraph(subtitle, font(14, Font.BOLD, TEXT));
        documentType.setAlignment(Element.ALIGN_CENTER);
        documentType.setSpacingAfter(6);
        document.add(documentType);

        Paragraph school = new Paragraph("Colegio Manuel Gonzales Prada", font(10, Font.NORMAL, MUTED));
        school.setAlignment(Element.ALIGN_CENTER);
        school.setSpacingAfter(14);
        document.add(school);

        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setFixedHeight(4);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setBackgroundColor(AQUA);
        divider.addCell(cell);
        divider.setSpacingAfter(18);
        document.add(divider);
    }

    private void addSection(Document document, String title, PdfTableWriter tableWriter) throws DocumentException {
        Paragraph sectionTitle = new Paragraph(title, font(12, Font.BOLD, INK));
        sectionTitle.setSpacingBefore(8);
        sectionTitle.setSpacingAfter(8);
        document.add(sectionTitle);

        PdfPTable table = createFieldTable();
        tableWriter.write(table);
        table.setSpacingAfter(12);
        document.add(table);
    }

    private PdfPTable createFieldTable() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{34, 66});
        return table;
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
                "Documento generado por Yachay Campus Virtual\n"
                        + "Fecha y hora de generacion: " + formatDateTime(LocalDateTime.now()) + "\n"
                        + "Este documento es informativo y fue generado automaticamente por el sistema.",
                font(8, Font.NORMAL, MUTED)
        );
        footer.setSpacingBefore(22);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private void addRow(PdfPTable table, String label, Object value) {
        table.addCell(createCell(label, font(10, Font.BOLD, INK), SOFT));
        table.addCell(createCell(value != null ? String.valueOf(value) : "", font(10, Font.NORMAL, TEXT), Color.WHITE));
    }

    private PdfPCell createCell(String text, Font font, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setPadding(9);
        cell.setBackgroundColor(background);
        cell.setBorderColor(LINE);
        return cell;
    }

    private Font font(float size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
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
    private interface PdfContentWriter {
        void write(Document document) throws DocumentException;
    }

    @FunctionalInterface
    private interface PdfTableWriter {
        void write(PdfPTable table);
    }
}
