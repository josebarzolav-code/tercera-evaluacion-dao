package com.biblioteca.prestamo_service.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada del préstamo con validaciones.
 * La fecha límite debe ser posterior a la fecha de préstamo
 * (se valida en el servicio como regla de negocio).
 */
public class PrestamoDTO {

    @NotNull(message = "El id del lector es obligatorio")
    private Long idLector;

    @NotNull(message = "El id del libro es obligatorio")
    private Long idLibro;

    @NotNull(message = "La fecha de préstamo es obligatoria")
    private String fechaPrestamo;

    @NotNull(message = "La fecha límite es obligatoria")
    private String fechaLimite;

    public PrestamoDTO() {
    }

    public Long getIdLector() {
        return idLector;
    }

    public void setIdLector(Long idLector) {
        this.idLector = idLector;
    }

    public Long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(String fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
}
