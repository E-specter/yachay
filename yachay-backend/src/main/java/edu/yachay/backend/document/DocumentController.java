package edu.yachay.backend.document;

import edu.yachay.backend.document.dto.DocumentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
