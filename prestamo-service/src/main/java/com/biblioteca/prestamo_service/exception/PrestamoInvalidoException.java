package com.biblioteca.prestamo_service.exception;

/**
 * Excepción de negocio: el préstamo no se puede devolver
 * porque no está en estado ACTIVO (HTTP 409).
 */
public class PrestamoInvalidoException extends RuntimeException {

    public PrestamoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
