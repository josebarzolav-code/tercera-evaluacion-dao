package com.biblioteca.prestamo_service.model;

/**
 * Estados posibles de un préstamo.
 * ACTIVO: el libro está prestado; DEVUELTO: ya fue devuelto;
 * VENCIDO: pasó la fecha límite sin devolver.
 */
public enum EstadoPrestamo {
    ACTIVO,
    DEVUELTO,
    VENCIDO
}
