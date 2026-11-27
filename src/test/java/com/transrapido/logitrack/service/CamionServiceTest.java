package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.CamionRequest;
import com.transrapido.logitrack.exception.SolicitudInvalidaException;
import com.transrapido.logitrack.repository.CamionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import com.transrapido.logitrack.model.Camion;

// Mejora: prueba unitaria nueva para el registro de camiones (RF-01, Tabla 48).
@ExtendWith(MockitoExtension.class)
class CamionServiceTest {

    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private CamionService camionService;

    @Test
    @DisplayName("RF-01: no se registra un camion con una placa ya existente")
    void registrar_conPlacaExistente_lanzaError() {
        CamionRequest datos = new CamionRequest();
        datos.setPlaca("ABC-123");
        datos.setMarca("Volvo");
        datos.setModelo("FH 460");
        datos.setAnio(2022);
        datos.setCapacidad(30.0);
        when(camionRepository.existsByPlaca("ABC-123")).thenReturn(true);

        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class,
                () -> camionService.registrar(datos));

        assertEquals("La placa ya esta registrada", error.getMessage());
        verify(camionRepository, never()).save(any(Camion.class));
    }
}
