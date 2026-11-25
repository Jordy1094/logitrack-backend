package com.transrapido.logitrack.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias de la regla de negocio principal (RF-08, HU-05) - Capitulo 7.
// Se prueba el Service aislado con Mockito, sin base de datos ni servidor HTTP.
@ExtendWith(MockitoExtension.class)
class RutaServiceTest {

    @Mock
    private RutaRepository rutaRepository;
    @Mock
    private CamionRepository camionRepository;
    @Mock
    private ConductorRepository conductorRepository;

    @InjectMocks
    private RutaService rutaService;

    private Camion camion;
    private Conductor conductor;
    private RutaRequest datos;

    @BeforeEach
    void prepararDatos() {
        camion = new Camion("ABC-123", "Volvo", "FH 460", 2022, 12.5);
        conductor = new Conductor("10000001", "Luis", "Ramos", "Q12345678", "987654321");

        datos = new RutaRequest();
        datos.setCamionId(1L);
        datos.setConductorId(7L);
        datos.setOrigen("Almacen Lima Norte");
        datos.setDestino("Trujillo");
        datos.setFechaSalida(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("HU-05 CA1: asigna la ruta y el camion pasa a EN_RUTA")
    void asignarRuta_camionDisponible_creaRutaEnCurso() {
        when(camionRepository.findById(1L)).thenReturn(Optional.of(camion));
        when(conductorRepository.findById(7L)).thenReturn(Optional.of(conductor));
        when(rutaRepository.save(any(Ruta.class))).thenAnswer(inv -> inv.getArgument(0));

        Ruta ruta = rutaService.asignarRuta(datos);

        assertEquals(EstadoRuta.EN_CURSO, ruta.getEstado());
        assertEquals(EstadoCamion.EN_RUTA, camion.getEstado());
        verify(rutaRepository).save(any(Ruta.class));
    }

    @Test
    @DisplayName("HU-04 CA2: un camion en mantenimiento no puede recibir una ruta")
    void asignarRuta_camionEnMantenimiento_lanzaConflicto() {
        camion.setEstado(EstadoCamion.EN_MANTENIMIENTO);
        when(camionRepository.findById(1L)).thenReturn(Optional.of(camion));
        when(conductorRepository.findById(7L)).thenReturn(Optional.of(conductor));

        RecursoNoDisponibleException error = assertThrows(RecursoNoDisponibleException.class,
                () -> rutaService.asignarRuta(datos));

        assertEquals("El camion ABC-123 esta en mantenimiento y no puede asignarse", error.getMessage());
        verify(rutaRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU-05 CA2: un conductor con una ruta en curso no puede recibir otra")
    void asignarRuta_conductorConRutaActiva_lanzaConflicto() {
        Camion otroCamion = new Camion("XYZ-987", "Scania", "R450", 2021, 15.0);
        conductor.getRutas().add(new Ruta(otroCamion, conductor, "Lima", "Ica", LocalDateTime.now()));
        when(camionRepository.findById(1L)).thenReturn(Optional.of(camion));
        when(conductorRepository.findById(7L)).thenReturn(Optional.of(conductor));

        assertThrows(RecursoNoDisponibleException.class, () -> rutaService.asignarRuta(datos));
        assertEquals(EstadoCamion.DISPONIBLE, camion.getEstado());
    }

    @Test
    @DisplayName("Camion inexistente: responde 404")
    void asignarRuta_camionInexistente_lanzaNoEncontrado() {
        when(camionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> rutaService.asignarRuta(datos));
    }

    @Test
    @DisplayName("HU-06 CA1: finalizar devuelve el camion a DISPONIBLE")
    void finalizar_rutaEnCurso_liberaCamion() {
        camion.setEstado(EstadoCamion.EN_RUTA);
        Ruta ruta = new Ruta(camion, conductor, "Lima", "Trujillo", LocalDateTime.now());
        when(rutaRepository.findById(42L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(ruta)).thenReturn(ruta);

        rutaService.finalizar(42L);

        assertEquals(EstadoRuta.FINALIZADA, ruta.getEstado());
        assertEquals(EstadoCamion.DISPONIBLE, camion.getEstado());
    }

    @Test
    @DisplayName("HU-06 CA2: una ruta finalizada no se puede finalizar otra vez")
    void finalizar_rutaYaFinalizada_lanzaConflicto() {
        Ruta ruta = new Ruta(camion, conductor, "Lima", "Trujillo", LocalDateTime.now());
        ruta.setEstado(EstadoRuta.FINALIZADA);
        when(rutaRepository.findById(42L)).thenReturn(Optional.of(ruta));

        assertThrows(RecursoNoDisponibleException.class, () -> rutaService.finalizar(42L));
        verify(rutaRepository, never()).save(any());
    }
}
