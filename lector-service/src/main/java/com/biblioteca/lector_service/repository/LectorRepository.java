package com.biblioteca.lector_service.repository;

import com.biblioteca.lector_service.model.Lector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA del lector. Consulta personalizada por documento
 * mediante naming convention de Spring Data.
 */
@Repository
public interface LectorRepository extends JpaRepository<Lector, Long> {

    Optional<Lector> findByDocumento(String documento);
}
