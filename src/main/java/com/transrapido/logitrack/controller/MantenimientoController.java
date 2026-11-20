package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.model.Mantenimiento;
import com.transrapido.logitrack.service.MantenimientoService;
import org.springframework.web.bind.annotation.*;

// Tabla 35: /api/mantenimientos/{id}/ingreso y /cierre.
@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @PutMapping("/{id}/ingreso")
    public Mantenimiento registrarIngreso(@PathVariable Long id) {
        return mantenimientoService.registrarIngreso(id);
    }

    @PutMapping("/{id}/cierre")
    public Mantenimiento registrarCierre(@PathVariable Long id) {
        return mantenimientoService.registrarCierre(id);
    }
}
