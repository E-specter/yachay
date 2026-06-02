package edu.yachay.backend.identity.domain.exceptions;

/**
 * Excepción lanzada cuando hay un conflicto al crear o modificar un recurso.
 * Por ejemplo: intentar crear un usuario con un email que ya existe.
 */
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
