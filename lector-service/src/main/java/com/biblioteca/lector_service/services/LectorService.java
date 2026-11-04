package com.biblioteca.lector_service.services;

import com.biblioteca.lector_service.dto.LectorDTO;
import com.biblioteca.lector_service.exception.DatoDuplicadoException;
import com.biblioteca.lector_service.exception.RecursoNoEncontradoException;
import com.biblioteca.lector_service.model.Lector;
import com.biblioteca.lector_service.repository.LectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio del lector: CRUD con documento único.
 */
@Service
public class LectorService {

    private final LectorRepository repositorio;

    public LectorService(LectorRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Lector> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Lector buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lector", id));
    }

    @Transactional(readOnly = true)
    public Lector buscarPorDocumento(String documento) {
        return repositorio.findByDocumento(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un lector con documento " + documento));
    }

    @Transactional
    public Lector crear(LectorDTO dto) {
        repositorio.findByDocumento(dto.getDocumento()).ifPresent(existente -> {
            throw new DatoDuplicadoException(
                    "Ya existe un lector con documento " + dto.getDocumento());
        });

        Lector lector = new Lector();
        copiarDatos(lector, dto);
        return repositorio.save(lector);
    }

    @Transactional
    public Lector actualizar(Long id, LectorDTO dto) {
        Lector lector = buscarPorId(id);

        repositorio.findByDocumento(dto.getDocumento()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new DatoDuplicadoException(
                        "Ya existe otro lector con documento " + dto.getDocumento());
            }
        });

        copiarDatos(lector, dto);
        return repositorio.save(lector);
    }

    @Transactional
    public void eliminar(Long id) {
        Lector lector = buscarPorId(id);
        repositorio.delete(lector);
    }

    private void copiarDatos(Lector lector, LectorDTO dto) {
        lector.setDocumento(dto.getDocumento().trim());
        lector.setNombres(dto.getNombres().trim());
        lector.setApellidos(dto.getApellidos().trim());
        lector.setCorreo(dto.getCorreo().trim());
        lector.setTelefono(dto.getTelefono() == null ? null : dto.getTelefono().trim());
    }
}
