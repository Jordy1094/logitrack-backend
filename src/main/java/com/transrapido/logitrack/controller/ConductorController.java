package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.dto.ConductorRequest;
import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.service.ConductorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Tabla 34: /api/conductores.
@RestController
@RequestMapping("/api/conductores")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    public List<Conductor> listar(@RequestParam(required = false) Boolean disponible) {
        return conductorService.listar(disponible);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Conductor registrar(@Valid @RequestBody ConductorRequest datos) {
        return conductorService.registrar(datos);
    }
}
