package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.dto.RutaRequest;
import com.transrapido.logitrack.exception.RecursoNoEncontradoException;
import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.model.Ruta;
import com.transrapido.logitrack.repository.ConductorRepository;
import com.transrapido.logitrack.service.RutaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Tabla 35: /api/rutas (asignacion, finalizacion) y /api/rutas/mia para el conductor autenticado.
@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;
    private final ConductorRepository conductorRepository;

    public RutaController(RutaService rutaService, ConductorRepository conductorRepository) {
        this.rutaService = rutaService;
        this.conductorRepository = conductorRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ruta asignar(@Valid @RequestBody RutaRequest datos) {
        return rutaService.asignarRuta(datos);
    }

    @PutMapping("/{id}/finalizar")
    public Ruta finalizar(@PathVariable Long id) {
        return rutaService.finalizar(id);
    }

    @GetMapping("/mia")
    public Object miRuta(Authentication authentication) {
        String correo = authentication.getName();
        Conductor conductor = conductorRepository.findByUsuario_Correo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Conductor no encontrado"));

        return rutaService.obtenerRutaDelConductor(conductor.getId())
                .map(ruta -> (Object) ruta)
                .orElse(Map.of("mensaje", "No tiene una ruta vigente"));
    }
}
