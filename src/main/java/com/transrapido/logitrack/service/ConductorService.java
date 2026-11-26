package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.ConductorRequest;
import com.transrapido.logitrack.dto.Pagina;
import com.transrapido.logitrack.exception.RecursoNoDisponibleException;
import com.transrapido.logitrack.exception.RecursoNoEncontradoException;
import com.transrapido.logitrack.exception.SolicitudInvalidaException;
import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.repository.ConductorRepository;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // GET /api/conductores?pagina=0&tamano=10 (RNF-17): la tabla de conductores se carga por
    // paginas, ordenada por apellidos; disponible=true deja solo los que no tienen ruta en curso.
    public Pagina<Conductor> listarPagina(String texto, Boolean disponible, int pagina, int tamano) {
        Pageable solicitud = Pagina.solicitud(pagina, tamano, Sort.by("apellidos", "nombres"));
        String busqueda = texto == null ? "" : texto.trim();
        if (Boolean.TRUE.equals(disponible)) {
            return Pagina.de(conductorRepository.buscarDisponibles(busqueda, EstadoRuta.EN_CURSO, solicitud));
        }
        return Pagina.de(conductorRepository.buscar(busqueda, solicitud));
    }

    public Conductor registrar(ConductorRequest datos) {
        if (conductorRepository.existsByDni(datos.getDni())) {
            throw new SolicitudInvalidaException("El DNI ya esta registrado");
        }
        Conductor conductor = new Conductor(datos.getDni(), datos.getNombres(), datos.getApellidos(),
                datos.getNroLicencia(), datos.getTelefono());
        return conductorRepository.save(conductor);
    }

    // PUT /api/conductores/{id} (RF-04): corrige los datos del conductor. El DNI identifica
    // a la persona y no se cambia, igual que la placa del camion.
    public Conductor actualizar(Long id, ConductorRequest datos) {
        Conductor conductor = obtenerOLanzar(id);
        conductor.setNombres(datos.getNombres());
        conductor.setApellidos(datos.getApellidos());
        conductor.setNroLicencia(datos.getNroLicencia());
        conductor.setTelefono(datos.getTelefono());
        return conductorRepository.save(conductor);
    }

    // DELETE /api/conductores/{id} (RF-04): no se da de baja a un conductor con una ruta en
    // curso, ni a uno con rutas en su historial (se perderia la trazabilidad de esas rutas).
    public void darDeBaja(Long id) {
        Conductor conductor = obtenerOLanzar(id);
        if (conductor.tieneRutaActiva()) {
            throw new RecursoNoDisponibleException("Primero debe finalizarse la ruta del conductor");
        }
        if (!conductor.getRutas().isEmpty()) {
            throw new RecursoNoDisponibleException("No se puede dar de baja: el conductor tiene rutas en su historial");
        }
        conductorRepository.delete(conductor);
    }

    public Conductor obtenerOLanzar(Long id) {
        return conductorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Conductor no encontrado"));
    }
}
