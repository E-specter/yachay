package edu.yachay.backend.document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IlovePdfClient {

    private final String publicKey;
    private final String secretKey;

    public IlovePdfClient(
            @Value("${ilovepdf.public-key:}") String publicKey,
            @Value("${ilovepdf.secret-key:}") String secretKey
    ) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    public boolean isConfigured() {
        return StringUtils.hasText(publicKey) && StringUtils.hasText(secretKey);
    }

    public String prepareGeneration(String documentType, Long entityId) {
        if (!isConfigured()) {
            return "CONFIGURACION_PENDIENTE: I Love PDF no esta configurado; los PDF basicos usan OpenPDF local.";
        }

        return "CONFIGURACION_PENDIENTE: I Love PDF tiene credenciales, pero no fue validado como servicio operativo.";
    }
}
