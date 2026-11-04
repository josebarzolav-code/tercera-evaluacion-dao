package com.biblioteca.lector_service.exception;

/**
 * Excepción de negocio: dato único duplicado (HTTP 409).
 */
public class DatoDuplicadoException extends RuntimeException {

    public DatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
