package com.transrapido.logitrack.service;

import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.Ruta;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import com.transrapido.logitrack.repository.CamionRepository;
import com.transrapido.logitrack.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// GET /api/dashboard/resumen (Tabla 36, HU-10): resumen de la flota para el administrador y el supervisor.
@Service
public class DashboardService {

    private final CamionRepository camionRepository;
    private final RutaRepository rutaRepository;

    public DashboardService(CamionRepository camionRepository, RutaRepository rutaRepository) {
        this.camionRepository = camionRepository;
        this.rutaRepository = rutaRepository;
    }

    public Map<String, Object> resumen() {
        List<Camion> camiones = camionRepository.findAll();
        Map<EstadoCamion, Long> camionesPorEstado = camiones.stream()
                .collect(Collectors.groupingBy(Camion::getEstado, Collectors.counting()));

        List<Ruta> rutasEnCurso = rutaRepository.findByEstado(EstadoRuta.EN_CURSO);

        List<Camion> novedades = camiones.stream()
                .filter(Camion::isConNovedad)
                .toList();

        return Map.of(
                "camionesPorEstado", camionesPorEstado,
                "rutasEnCurso", rutasEnCurso.size(),
                "novedadesReportadas", novedades.size()
        );
    }
}
