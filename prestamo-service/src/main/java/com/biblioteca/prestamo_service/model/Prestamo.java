package com.biblioteca.prestamo_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que representa un préstamo de un libro a un lector.
 * Se persiste en la base de datos bd_prestamo (tabla prestamos).
 *
 * OJO: idLector e idLibro son solo REFERENCIAS (IDs) a los microservicios
 * lector-service y libro-service; no se copian sus datos aquí porque
 * cada microservicio tiene su propia base de datos.
 */
@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_lector", nullable = false)
    private Long idLector;

    @Column(name = "id_libro", nullable = false)
    private Long idLibro;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPrestamo estado;

    public Prestamo() {
    }

    public Prestamo(Long idLector, Long idLibro, LocalDate fechaPrestamo,
                    LocalDate fechaLimite, EstadoPrestamo estado) {
        this.idLector = idLector;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }
}
