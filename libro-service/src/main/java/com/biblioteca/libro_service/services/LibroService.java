package com.biblioteca.libro_service.services;

import com.biblioteca.libro_service.dto.LibroDTO;
import com.biblioteca.libro_service.exception.DatoDuplicadoException;
import com.biblioteca.libro_service.exception.RecursoNoEncontradoException;
import com.biblioteca.libro_service.model.EstadoLibro;
import com.biblioteca.libro_service.model.Libro;
import com.biblioteca.libro_service.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio del libro: CRUD, reglas de negocio
 * (ISBN único, stock no negativo, estado derivado del stock).
 */
@Service
public class LibroService {

    private final LibroRepository repositorio;

    public LibroService(LibroRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Libro> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Libro buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro", id));
    }

    @Transactional
    public Libro crear(LibroDTO dto) {
        repositorio.findByIsbn(dto.getIsbn()).ifPresent(existente -> {
            throw new DatoDuplicadoException("Ya existe un libro con ISBN " + dto.getIsbn());
        });

        Libro libro = new Libro();
        libro.setIsbn(dto.getIsbn().trim());
        libro.setTitulo(dto.getTitulo().trim());
        libro.setAutor(dto.getAutor().trim());
        libro.setStock(dto.getStock());
        libro.setEstado(calcularEstado(dto.getStock(), dto.getEstado()));
        return repositorio.save(libro);
    }

    @Transactional
    public Libro actualizar(Long id, LibroDTO dto) {
        Libro libro = buscarPorId(id);

        repositorio.findByIsbn(dto.getIsbn()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new DatoDuplicadoException("Ya existe otro libro con ISBN " + dto.getIsbn());
            }
        });

        libro.setIsbn(dto.getIsbn().trim());
        libro.setTitulo(dto.getTitulo().trim());
        libro.setAutor(dto.getAutor().trim());
        libro.setStock(dto.getStock());
        libro.setEstado(calcularEstado(dto.getStock(), dto.getEstado()));
        return repositorio.save(libro);
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = buscarPorId(id);
        repositorio.delete(libro);
    }

    @Transactional(readOnly = true)
    public Libro buscarPorIsbn(String isbn) {
        return repositorio.findByIsbn(isbn)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un libro con ISBN " + isbn));
    }

    /**
     * El estado INACTIVO puede indicarse explícitamente. En los demás
     * casos, el estado se deriva del stock disponible.
     */
    private EstadoLibro calcularEstado(Integer stock, String estadoSolicitado) {
        if (estadoSolicitado != null && !estadoSolicitado.isBlank()
                && EstadoLibro.valueOf(estadoSolicitado.trim().toUpperCase()) == EstadoLibro.INACTIVO) {
            return EstadoLibro.INACTIVO;
        }
        if (stock == null || stock <= 0) {
            return EstadoLibro.AGOTADO;
        }
        return EstadoLibro.DISPONIBLE;
    }
}



