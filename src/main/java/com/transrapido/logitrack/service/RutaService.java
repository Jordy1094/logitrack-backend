package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.Pagina;
import com.transrapido.logitrack.dto.RutaRequest;
import com.transrapido.logitrack.exception.RecursoNoDisponibleException;
import com.transrapido.logitrack.exception.RecursoNoEncontradoException;
import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.model.Ruta;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import com.transrapido.logitrack.repository.CamionRepository;
import com.transrapido.logitrack.repository.ConductorRepository;
import com.transrapido.logitrack.repository.RutaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Asignacion de rutas (Tabla 35, seccion 5.1 del informe): la regla mas importante
// del capitulo vive aqui, no en el Controller (RF-08, HU-05 - CA2).
@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final CamionRepository camionRepository;
    private final ConductorRepository conductorRepository;

    public RutaService(RutaRepository rutaRepository, CamionRepository camionRepository,
                        ConductorRepository conductorRepository) {
        this.rutaRepository = rutaRepository;
        this.camionRepository = camionRepository;
        this.conductorRepository = conductorRepository;
    }

    public Ruta asignarRuta(RutaRequest datos) {
        Camion camion = camionRepository.findById(datos.getCamionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Camion no encontrado"));
        Conductor conductor = conductorRepository.findById(datos.getConductorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Conductor no encontrado"));

        // Figura 9 (wireframe): el mensaje explica el motivo concreto en lenguaje del usuario.
        if (camion.getEstado() == EstadoCamion.EN_MANTENIMIENTO) {
            throw new RecursoNoDisponibleException(
                    "El camion " + camion.getPlaca() + " esta en mantenimiento y no puede asignarse");
        }
        if (camion.getEstado() != EstadoCamion.DISPONIBLE) {
            throw new RecursoNoDisponibleException("El camion ya esta asignado a otra ruta");
        }
        if (conductor.tieneRutaActiva()) {
            throw new RecursoNoDisponibleException("El conductor ya tiene una ruta en curso");
        }

        Ruta ruta = new Ruta(camion, conductor, datos.getOrigen(), datos.getDestino(), datos.getFechaSalida());
        camion.setEstado(EstadoCamion.EN_RUTA);
        return rutaRepository.save(ruta);
    }

    // GET /api/rutas (Capitulo 6): el Dashboard lista las rutas en curso para poder
    // finalizarlas (RF-14); sin filtro devuelve todas, de la mas reciente a la mas antigua.
    public List<Ruta> listar(EstadoRuta estado) {
        if (estado != null) {
            return rutaRepository.findByEstadoOrderByFechaSalidaDesc(estado);
        }
        return rutaRepository.findAllByOrderByFechaSalidaDesc();
    }

    // GET /api/rutas?pagina=0&tamano=5 (RNF-17): rutas por paginas, de la mas reciente a la mas antigua.
    public Pagina<Ruta> listarPagina(EstadoRuta estado, int pagina, int tamano) {
        Pageable solicitud = Pagina.solicitud(pagina, tamano, Sort.by(Sort.Direction.DESC, "fechaSalida"));
        if (estado != null) {
            return Pagina.de(rutaRepository.findByEstado(estado, solicitud));
        }
        return Pagina.de(rutaRepository.findAll(solicitud));
    }

    public Ruta finalizar(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta no encontrada"));
        if (ruta.getEstado() == EstadoRuta.FINALIZADA) {
            throw new RecursoNoDisponibleException("La ruta ya estaba finalizada");
        }
        ruta.setEstado(EstadoRuta.FINALIZADA);
        ruta.getCamion().setEstado(EstadoCamion.DISPONIBLE);
        return rutaRepository.save(ruta);
    }

    public Optional<Ruta> obtenerRutaDelConductor(Long conductorId) {
        return rutaRepository.findFirstByConductor_IdAndEstado(conductorId, EstadoRuta.EN_CURSO);
    }
}
