package com.transrapido.logitrack.service;

import com.transrapido.logitrack.exception.RecursoNoDisponibleException;
import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.Mantenimiento;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import com.transrapido.logitrack.model.enums.EstadoMantenimiento;
import com.transrapido.logitrack.model.enums.TipoMantenimiento;
import com.transrapido.logitrack.repository.CamionRepository;
import com.transrapido.logitrack.repository.MantenimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias del ciclo de mantenimiento (RF-11, RF-12, HU-08) - Capitulo 7.
@ExtendWith(MockitoExtension.class)
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;
    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private MantenimientoService mantenimientoService;

    private Camion camion;
    private Mantenimiento mantenimiento;

    @BeforeEach
    void prepararDatos() {
        camion = new Camion("ABC-123", "Volvo", "FH 460", 2022, 12.5);
        mantenimiento = new Mantenimiento(camion, TipoMantenimiento.PREVENTIVO,
                LocalDate.now().plusDays(2), "Cambio de aceite", "Taller Central");
    }

    @Test
    @DisplayName("HU-08 CA1: el ingreso al taller pone el camion en EN_MANTENIMIENTO")
    void registrarIngreso_camionDisponible_pasaAMantenimiento() {
        when(mantenimientoRepository.findById(5L)).thenReturn(Optional.of(mantenimiento));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        mantenimientoService.registrarIngreso(5L);

        assertEquals(EstadoCamion.EN_MANTENIMIENTO, camion.getEstado());
        assertEquals(EstadoMantenimiento.EN_TALLER, mantenimiento.getEstado());
        assertNotNull(mantenimiento.getFechaIngreso());
    }

    @Test
    @DisplayName("HU-08 CA2: un camion en ruta no puede ingresar al taller")
    void registrarIngreso_camionEnRuta_lanzaConflicto() {
        camion.setEstado(EstadoCamion.EN_RUTA);
        when(mantenimientoRepository.findById(5L)).thenReturn(Optional.of(mantenimiento));

        assertThrows(RecursoNoDisponibleException.class, () -> mantenimientoService.registrarIngreso(5L));
        assertEquals(EstadoCamion.EN_RUTA, camion.getEstado());
    }

    @Test
    @DisplayName("HU-08 CA3: el cierre devuelve el camion a DISPONIBLE")
    void registrarCierre_mantenimientoEnTaller_liberaCamion() {
        camion.setEstado(EstadoCamion.EN_MANTENIMIENTO);
        mantenimiento.setEstado(EstadoMantenimiento.EN_TALLER);
        when(mantenimientoRepository.findById(5L)).thenReturn(Optional.of(mantenimiento));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        mantenimientoService.registrarCierre(5L);

        assertEquals(EstadoMantenimiento.CERRADO, mantenimiento.getEstado());
        assertEquals(EstadoCamion.DISPONIBLE, camion.getEstado());
    }

    @Test
    @DisplayName("Incidencia I-04: no se puede cerrar un mantenimiento que no esta en el taller")
    void registrarCierre_mantenimientoProgramado_lanzaConflicto() {
        when(mantenimientoRepository.findById(5L)).thenReturn(Optional.of(mantenimiento));

        assertThrows(RecursoNoDisponibleException.class, () -> mantenimientoService.registrarCierre(5L));
        verify(mantenimientoRepository, never()).save(any());
    }
}
