package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.CamionRequest;
import com.transrapido.logitrack.dto.ReporteRequest;
import com.transrapido.logitrack.exception.RecursoNoDisponibleException;
import com.transrapido.logitrack.exception.RecursoNoEncontradoException;
import com.transrapido.logitrack.exception.SolicitudInvalidaException;
import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import com.transrapido.logitrack.repository.CamionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Reglas de negocio de los camiones (Tabla 34, filas de /api/camiones): registrar,
// listar, editar y dar de baja, siempre validando el estado antes de cambiarlo.
@Service
public class CamionService {

    private final CamionRepository camionRepository;

    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    public List<Camion> listar(EstadoCamion estado, String placa) {
        if (estado != null) {
            return camionRepository.findByEstado(estado);
        }
        if (placa != null && !placa.isBlank()) {
            return camionRepository.findByPlaca(placa).map(List::of).orElseGet(List::of);
        }
        return camionRepository.findAll();
    }

    public List<Camion> listarDisponibles() {
        return camionRepository.findByEstado(EstadoCamion.DISPONIBLE);
    }

    public Camion registrar(CamionRequest datos) {
        if (camionRepository.existsByPlaca(datos.getPlaca())) {
            throw new SolicitudInvalidaException("La placa ya esta registrada");
        }
        Camion camion = new Camion(datos.getPlaca(), datos.getMarca(), datos.getModelo(), datos.getAnio(), datos.getCapacidad());
        return camionRepository.save(camion);
    }

    public Camion actualizar(Long id, CamionRequest datos) {
        Camion camion = obtenerOLanzar(id);
        camion.setMarca(datos.getMarca());
        camion.setModelo(datos.getModelo());
        camion.setAnio(datos.getAnio());
        camion.setCapacidad(datos.getCapacidad());
        return camionRepository.save(camion);
    }

    public void darDeBaja(Long id) {
        Camion camion = obtenerOLanzar(id);
        if (camion.getEstado() == EstadoCamion.EN_RUTA) {
            throw new RecursoNoDisponibleException("Primero debe finalizarse la ruta");
        }
        camionRepository.delete(camion);
    }

    // POST /api/camiones/{id}/reportes (Tabla 36): el conductor reporta el estado
    // del vehiculo; si hay novedad, la descripcion es obligatoria (CASOS_POSTMAN).
    public Camion registrarReporte(Long id, ReporteRequest datos) {
        Camion camion = obtenerOLanzar(id);
        if (Boolean.TRUE.equals(datos.getConNovedad())
                && (datos.getDescripcion() == null || datos.getDescripcion().isBlank())) {
            throw new SolicitudInvalidaException("La descripcion es obligatoria cuando hay novedad");
        }
        camion.setConNovedad(Boolean.TRUE.equals(datos.getConNovedad()));
        camion.setDescripcionNovedad(camion.isConNovedad() ? datos.getDescripcion() : null);
        return camionRepository.save(camion);
    }

    public Camion obtenerOLanzar(Long id) {
        return camionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Camion no encontrado"));
    }
}
