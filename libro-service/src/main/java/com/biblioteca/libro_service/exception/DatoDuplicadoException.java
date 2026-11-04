package com.biblioteca.libro_service.exception;

/**
 * Excepción de negocio que se lanza cuando se intenta guardar un registro
 * con un dato único que ya existe (por ejemplo, el ISBN de un libro).
 * Se convierte en una respuesta HTTP 409 Conflict.
 */
public class DatoDuplicadoException extends RuntimeException {

    public DatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
