package edu.yachay.backend.identity.domain.exceptions;

/**
 * Excepción lanzada cuando un recurso no es encontrado en el sistema.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
