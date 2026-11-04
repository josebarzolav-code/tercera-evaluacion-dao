package com.biblioteca.prestamo_service.repository;

import com.biblioteca.prestamo_service.model.EstadoPrestamo;
import com.biblioteca.prestamo_service.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA del préstamo con consultas personalizadas:
 * por lector y por estado (naming convention de Spring Data).
 */
@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByIdLector(Long idLector);

    List<Prestamo> findByEstado(EstadoPrestamo estado);
}
