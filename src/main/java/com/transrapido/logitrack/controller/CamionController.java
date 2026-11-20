package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.dto.CamionRequest;
import com.transrapido.logitrack.dto.MantenimientoRequest;
import com.transrapido.logitrack.dto.ReporteRequest;
import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.Mantenimiento;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import com.transrapido.logitrack.service.CamionService;
import com.transrapido.logitrack.service.MantenimientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Tablas 34 a 36: /api/camiones (CRUD, disponibilidad) y los mantenimientos/reportes propios de un camion.
@RestController
@RequestMapping("/api/camiones")
public class CamionController {

    private final CamionService camionService;
    private final MantenimientoService mantenimientoService;

    public CamionController(CamionService camionService, MantenimientoService mantenimientoService) {
        this.camionService = camionService;
        this.mantenimientoService = mantenimientoService;
    }

    @GetMapping
    public List<Camion> listar(@RequestParam(required = false) EstadoCamion estado,
                                @RequestParam(required = false) String placa) {
        return camionService.listar(estado, placa);
    }

    @GetMapping("/disponibles")
    public List<Camion> listarDisponibles() {
        return camionService.listarDisponibles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Camion registrar(@Valid @RequestBody CamionRequest datos) {
        return camionService.registrar(datos);
    }

    @PutMapping("/{id}")
    public Camion actualizar(@PathVariable Long id, @Valid @RequestBody CamionRequest datos) {
        return camionService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) {
        camionService.darDeBaja(id);
    }

    @PostMapping("/{id}/mantenimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mantenimiento programarMantenimiento(@PathVariable Long id,
                                                 @Valid @RequestBody MantenimientoRequest datos) {
        return mantenimientoService.programar(id, datos);
    }

    @GetMapping("/{id}/mantenimientos")
    public List<Mantenimiento> historialMantenimientos(@PathVariable Long id) {
        return mantenimientoService.historial(id);
    }

    @PostMapping("/{id}/reportes")
    @ResponseStatus(HttpStatus.CREATED)
    public Camion registrarReporte(@PathVariable Long id, @Valid @RequestBody ReporteRequest datos) {
        return camionService.registrarReporte(id, datos);
    }
}
