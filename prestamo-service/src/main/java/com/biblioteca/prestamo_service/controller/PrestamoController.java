package com.biblioteca.prestamo_service.controller;

import com.biblioteca.prestamo_service.dto.PrestamoDTO;
import com.biblioteca.prestamo_service.model.EstadoPrestamo;
import com.biblioteca.prestamo_service.model.Prestamo;
import com.biblioteca.prestamo_service.services.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * API REST del microservicio de préstamos (puerto 8083).
 * Endpoints: GET, GET por ID, GET por lector, GET por estado,
 * POST (registrar préstamo), PATCH devolución y DELETE.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService servicio;

    public PrestamoController(PrestamoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Prestamo> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Prestamo buscarPorId(@PathVariable Long id) {
        return servicio.buscarPorId(id);
    }

    @GetMapping("/lector/{idLector}")
    public List<Prestamo> listarPorLector(@PathVariable Long idLector) {
        return servicio.listarPorLector(idLector);
    }

    @GetMapping("/estado/{estado}")
    public List<Prestamo> listarPorEstado(@PathVariable EstadoPrestamo estado) {
        return servicio.listarPorEstado(estado);
    }

    @PostMapping
    public ResponseEntity<Prestamo> crear(@Valid @RequestBody PrestamoDTO dto) {
        Prestamo creado = servicio.crear(dto);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    /**
     * Devolución del libro: cambia el estado del préstamo a DEVUELTO.
     */
    @PatchMapping("/{id}/devolucion")
    public Prestamo devolver(@PathVariable Long id) {
        return servicio.devolver(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
