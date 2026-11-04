package com.biblioteca.lector_service.controller;

import com.biblioteca.lector_service.dto.LectorDTO;
import com.biblioteca.lector_service.model.Lector;
import com.biblioteca.lector_service.services.LectorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * API REST del microservicio de lectores (puerto 8082).
 * Endpoints: GET, GET por ID, GET por documento, POST, PUT y DELETE.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/lectores")
public class LectorController {

    private final LectorService servicio;

    public LectorController(LectorService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Lector> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Lector buscarPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @GetMapping("/documento/{documento}")
    public Lector buscarPorDocumento(@PathVariable String documento) {
        return servicio.buscarPorDocumento(documento);
    }

    @PostMapping
    public ResponseEntity<Lector> crear(@Valid @RequestBody LectorDTO dto) {
        Lector creado = servicio.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    public Lector actualizar(@PathVariable Long id, @Valid @RequestBody LectorDTO dto) {
        return servicio.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
