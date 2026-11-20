package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.ConductorRequest;
import com.transrapido.logitrack.exception.RecursoNoEncontradoException;
import com.transrapido.logitrack.exception.SolicitudInvalidaException;
import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.repository.ConductorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Reglas de negocio de los conductores (Tabla 34, filas de /api/conductores).
@Service
public class ConductorService {

    private final ConductorRepository conductorRepository;

    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    public List<Conductor> listar(Boolean disponible) {
        List<Conductor> todos = conductorRepository.findAll();
        if (disponible == null) {
            return todos;
        }
        return todos.stream()
                .filter(c -> disponible != c.tieneRutaActiva())
                .toList();
    }

    public Conductor registrar(ConductorRequest datos) {
        if (conductorRepository.existsByDni(datos.getDni())) {
            throw new SolicitudInvalidaException("El DNI ya esta registrado");
        }
        Conductor conductor = new Conductor(datos.getDni(), datos.getNombres(), datos.getApellidos(),
                datos.getNroLicencia(), datos.getTelefono());
        return conductorRepository.save(conductor);
    }

    public Conductor obtenerOLanzar(Long id) {
        return conductorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Conductor no encontrado"));
    }
}
