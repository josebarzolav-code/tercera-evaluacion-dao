package com.biblioteca.prestamo_service.services;

import com.biblioteca.prestamo_service.dto.PrestamoDTO;
import com.biblioteca.prestamo_service.exception.PrestamoInvalidoException;
import com.biblioteca.prestamo_service.exception.RecursoNoEncontradoException;
import com.biblioteca.prestamo_service.model.EstadoPrestamo;
import com.biblioteca.prestamo_service.model.Prestamo;
import com.biblioteca.prestamo_service.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Servicio de negocio del préstamo:
 * - Valida que la fecha límite sea posterior a la fecha de préstamo.
 * - Registra el préstamo en estado ACTIVO.
 * - Devuelve el libro cambiando el estado a DEVUELTO.
 */
@Service
public class PrestamoService {

    private final PrestamoRepository repositorio;

    public PrestamoService(PrestamoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Prestamo buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo", id));
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPorLector(Long idLector) {
        return repositorio.findByIdLector(idLector);
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPorEstado(EstadoPrestamo estado) {
        return repositorio.findByEstado(estado);
    }

    @Transactional
    public Prestamo crear(PrestamoDTO dto) {
        LocalDate fechaPrestamo = convertir(dto.getFechaPrestamo(), "fecha de préstamo");
        LocalDate fechaLimite = convertir(dto.getFechaLimite(), "fecha límite");

        // Regla de negocio: la fecha límite debe ser posterior a la fecha de préstamo
        if (!fechaLimite.isAfter(fechaPrestamo)) {
            throw new IllegalArgumentException(
                    "La fecha límite debe ser posterior a la fecha de préstamo");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setIdLector(dto.getIdLector());
        prestamo.setIdLibro(dto.getIdLibro());
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaLimite(fechaLimite);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        return repositorio.save(prestamo);
    }

    /**
     * Devolución del libro: cambia el estado a DEVUELTO.
     * Si ya fue devuelto, responde 409 (conflicto).
     */
    @Transactional
    public Prestamo devolver(Long id) {
        Prestamo prestamo = buscarPorId(id);

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new PrestamoInvalidoException(
                    "El préstamo con id " + id + " ya fue devuelto");
        }

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        return repositorio.save(prestamo);
    }

    @Transactional
    public void eliminar(Long id) {
        Prestamo prestamo = buscarPorId(id);
        repositorio.delete(prestamo);
    }

    private LocalDate convertir(String texto, String nombreCampo) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "El formato de la " + nombreCampo + " debe ser yyyy-MM-dd (ej: 2026-11-05)");
        }
    }
}
