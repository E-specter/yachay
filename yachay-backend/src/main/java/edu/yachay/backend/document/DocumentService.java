package edu.yachay.backend.document;

import edu.yachay.backend.document.dto.DocumentResponse;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final IlovePdfClient ilovePdfClient;

    public DocumentService(IlovePdfClient ilovePdfClient) {
        this.ilovePdfClient = ilovePdfClient;
    }

    public DocumentResponse generateAdmissionPdf(Long admissionId) {
        return response("postulacion", admissionId);
    }

    public DocumentResponse generateStudentPdf(Long studentId) {
        return response("alumno", studentId);
    }

    private DocumentResponse response(String documentType, Long entityId) {
        boolean configured = ilovePdfClient.isConfigured();
        String message = ilovePdfClient.prepareGeneration(documentType, entityId);

        return new DocumentResponse(configured, message, documentType, entityId, configured);
    }
}
