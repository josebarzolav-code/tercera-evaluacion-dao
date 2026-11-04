package com.biblioteca.libro_service.repository;

import com.biblioteca.libro_service.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA del libro. Expone operaciones CRUD sobre la tabla libros
 * y una consulta personalizada por ISBN mediante naming convention de Spring Data.
 */
@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByIsbn(String isbn);
}
