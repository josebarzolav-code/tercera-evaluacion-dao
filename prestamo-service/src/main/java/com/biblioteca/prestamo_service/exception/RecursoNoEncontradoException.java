package com.biblioteca.prestamo_service.exception;

/**
 * Excepción de negocio: recurso inexistente (HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no existe");
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
