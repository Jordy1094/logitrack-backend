package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.ConductorRequest;
import com.transrapido.logitrack.exception.RecursoNoDisponibleException;
import com.transrapido.logitrack.exception.SolicitudInvalidaException;
import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.model.Ruta;
import com.transrapido.logitrack.repository.ConductorRepository;
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

// Pruebas unitarias de la edicion y baja de conductores (RF-04).
@ExtendWith(MockitoExtension.class)
class ConductorServiceTest {

    @Mock
    private ConductorRepository conductorRepository;

    @InjectMocks
    private ConductorService conductorService;

    private Conductor conductor;

    @BeforeEach
    void prepararDatos() {
        conductor = new Conductor("45678912", "Ana", "Torres", "Q87654321", "912345678");
    }

    @Test
    @DisplayName("RF-04: editar cambia los datos y conserva el DNI")
    void actualizar_cambiaDatosYConservaDni() {
        ConductorRequest datos = new ConductorRequest();
        datos.setDni("99999999");
        datos.setNombres("Ana Maria");
        datos.setApellidos("Torres Diaz");
        datos.setNroLicencia("Q11111111");
        datos.setTelefono("987000111");
        when(conductorRepository.findById(5L)).thenReturn(Optional.of(conductor));
        when(conductorRepository.save(any(Conductor.class))).thenAnswer(inv -> inv.getArgument(0));

        Conductor actualizado = conductorService.actualizar(5L, datos);

        assertEquals("Ana Maria", actualizado.getNombres());
        assertEquals("Q11111111", actualizado.getNroLicencia());
        assertEquals("45678912", actualizado.getDni());
    }

    @Test
    @DisplayName("RF-04: no se da de baja a un conductor con una ruta en curso")
    void darDeBaja_conRutaEnCurso_lanzaConflicto() {
        Camion camion = new Camion("ABC-123", "Volvo", "FH 460", 2022, 30.0);
        conductor.getRutas().add(new Ruta(camion, conductor, "Lima", "Trujillo", LocalDateTime.now()));
        when(conductorRepository.findById(5L)).thenReturn(Optional.of(conductor));

        RecursoNoDisponibleException error = assertThrows(RecursoNoDisponibleException.class,
                () -> conductorService.darDeBaja(5L));

        assertEquals("Primero debe finalizarse la ruta del conductor", error.getMessage());
        verify(conductorRepository, never()).delete(any());
    }

    @Test
    @DisplayName("RF-04: un conductor sin rutas se da de baja")
    void darDeBaja_sinRutas_elimina() {
        when(conductorRepository.findById(5L)).thenReturn(Optional.of(conductor));

        conductorService.darDeBaja(5L);

        verify(conductorRepository).delete(conductor);
    }

    // Mejora: prueba unitaria nueva para el registro de conductores (RF-04, Tabla 48).
    @Test
    @DisplayName("RF-04: no se registra un conductor con un DNI ya existente")
    void registrar_conDniExistente_lanzaError() {
        ConductorRequest datos = new ConductorRequest();
        datos.setDni("45678912");
        datos.setNombres("Carlos");
        datos.setApellidos("Mendoza");
        datos.setNroLicencia("Q99999999");
        datos.setTelefono("987654321");
        when(conductorRepository.existsByDni("45678912")).thenReturn(true);

        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class,
                () -> conductorService.registrar(datos));

        assertEquals("El DNI ya esta registrado", error.getMessage());
        verify(conductorRepository, never()).save(any(Conductor.class));
    }
}
