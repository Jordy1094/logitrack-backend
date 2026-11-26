package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.dto.ConductorRequest;
import com.transrapido.logitrack.dto.Pagina;
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

    // RNF-17: con el parametro pagina, la misma URL devuelve una pagina (Pagina<Conductor>).
    @GetMapping(params = "pagina")
    public Pagina<Conductor> listarPagina(@RequestParam(required = false) String texto,
                                          @RequestParam(required = false) Boolean disponible,
                                          @RequestParam int pagina,
                                          @RequestParam(defaultValue = "10") int tamano) {
        return conductorService.listarPagina(texto, disponible, pagina, tamano);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Conductor registrar(@Valid @RequestBody ConductorRequest datos) {
        return conductorService.registrar(datos);
    }

    @PutMapping("/{id}")
    public Conductor actualizar(@PathVariable Long id, @Valid @RequestBody ConductorRequest datos) {
        return conductorService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable Long id) {
        conductorService.darDeBaja(id);
    }
}
