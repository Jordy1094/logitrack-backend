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
