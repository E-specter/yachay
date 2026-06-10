package edu.yachay.backend.document;

import edu.yachay.backend.document.dto.DocumentResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/documentos")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/postulacion/{id}/pdf")
    public ResponseEntity<DocumentResponse> generateAdmissionPdf(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.generateAdmissionPdf(id));
    }

    @PostMapping("/alumno/{id}/pdf")
    public ResponseEntity<DocumentResponse> generateStudentPdf(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.generateStudentPdf(id));
    }

    @GetMapping("/postulacion/{id}/pdf")
    public ResponseEntity<byte[]> downloadAdmissionPdf(@PathVariable Long id) {
        return pdf(documentService.buildAdmissionPdf(id), "postulacion-" + id + ".pdf");
    }

    @GetMapping("/alumno/{id}/pdf")
    public ResponseEntity<byte[]> downloadStudentPdf(@PathVariable Integer id) {
        return pdf(documentService.buildStudentPdf(id), "ficha-alumno-" + id + ".pdf");
    }

    private ResponseEntity<byte[]> pdf(byte[] content, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString()
                )
                .body(content);
    }
}
