package com.biblioteca.prestamo_service.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de errores (404, 409, 400) con mensajes comprensibles.
 */
@RestControllerAdvice
public class ManejoGlobalErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> recursoNoEncontrado(RecursoNoEncontradoException ex) {
        return armar(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(PrestamoInvalidoException.class)
    public ResponseEntity<ErrorRespuesta> prestamoInvalido(PrestamoInvalidoException ex) {
        return armar(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(f -> campos.put(f.getField(), f.getDefaultMessage()));
        return armar(HttpStatus.BAD_REQUEST, "Datos inválidos, revise los campos indicados", campos);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRespuesta> reglaDeNegocio(IllegalArgumentException ex) {
        return armar(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorRespuesta> estadoInvalido(IllegalStateException ex) {
        return armar(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> integridad(DataIntegrityViolationException ex) {
        return armar(HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad de datos", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> inesperado(Exception ex) {
        return armar(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado en el servidor", null);
    }

    private ResponseEntity<ErrorRespuesta> armar(HttpStatus estado, String mensaje,
                                                 Map<String, String> campos) {
        ErrorRespuesta cuerpo = new ErrorRespuesta(
                LocalDateTime.now(), estado.value(), estado.getReasonPhrase(), mensaje, campos);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
