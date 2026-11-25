package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.MantenimientoRequest;
import com.transrapido.logitrack.exception.RecursoNoDisponibleException;
import com.transrapido.logitrack.exception.RecursoNoEncontradoException;
import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.Mantenimiento;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import com.transrapido.logitrack.model.enums.EstadoMantenimiento;
import com.transrapido.logitrack.repository.CamionRepository;
import com.transrapido.logitrack.repository.MantenimientoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Programacion e historial de mantenimientos (Tabla 35, seccion 5.1): mientras un
// camion esta en el taller no puede recibir una ruta nueva (RF-10 a RF-12).
@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final CamionRepository camionRepository;

    public MantenimientoService(MantenimientoRepository mantenimientoRepository, CamionRepository camionRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.camionRepository = camionRepository;
    }

    public Mantenimiento programar(Long camionId, MantenimientoRequest datos) {
        Camion camion = camionRepository.findById(camionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Camion no encontrado"));
        Mantenimiento mantenimiento = new Mantenimiento(camion, datos.getTipo(), datos.getFechaProgramada(),
                datos.getMotivo(), datos.getTaller());
        return mantenimientoRepository.save(mantenimiento);
    }

    public Mantenimiento registrarIngreso(Long id) {
        Mantenimiento mantenimiento = obtenerOLanzar(id);
        if (mantenimiento.getEstado() != EstadoMantenimiento.PROGRAMADO) {
            throw new RecursoNoDisponibleException("El mantenimiento no esta programado (ya esta en el taller o fue cerrado)");
        }
        Camion camion = mantenimiento.getCamion();
        if (camion.getEstado() == EstadoCamion.EN_RUTA) {
            throw new RecursoNoDisponibleException("El camion esta en ruta");
        }
        camion.setEstado(EstadoCamion.EN_MANTENIMIENTO);
        mantenimiento.setEstado(EstadoMantenimiento.EN_TALLER);
        mantenimiento.setFechaIngreso(LocalDate.now());
        return mantenimientoRepository.save(mantenimiento);
    }

    public Mantenimiento registrarCierre(Long id) {
        Mantenimiento mantenimiento = obtenerOLanzar(id);
        // Tabla 9: solo se cierra un mantenimiento que esta en curso (camion en el taller).
        if (mantenimiento.getEstado() != EstadoMantenimiento.EN_TALLER) {
            throw new RecursoNoDisponibleException("Solo se puede cerrar un mantenimiento que esta en el taller");
        }
        mantenimiento.setEstado(EstadoMantenimiento.CERRADO);
        mantenimiento.setFechaCierre(LocalDate.now());
        mantenimiento.getCamion().setEstado(EstadoCamion.DISPONIBLE);
        return mantenimientoRepository.save(mantenimiento);
    }

    public List<Mantenimiento> historial(Long camionId) {
        return mantenimientoRepository.findByCamion_IdOrderByFechaProgramadaDesc(camionId);
    }

    private Mantenimiento obtenerOLanzar(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
    }
}
