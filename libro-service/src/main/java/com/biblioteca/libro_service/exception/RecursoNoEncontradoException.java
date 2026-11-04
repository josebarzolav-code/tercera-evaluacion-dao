package com.biblioteca.libro_service.exception;

/**
 * Excepción de negocio que se lanza cuando se busca un recurso por un ID
 * que no existe en la base de datos. Se convierte en una respuesta HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no existe");
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
