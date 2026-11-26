package com.transrapido.logitrack.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias de la carga por paginas (RNF-17).
class PaginaTest {

    @Test
    @DisplayName("Una pagina de 10 sobre 80 camiones informa 8 paginas en total")
    void paginaDeOchentaCamiones() {
        List<String> placas = List.of("ABC-101", "ABC-102", "ABC-103", "ABC-104", "ABC-105",
                "ABC-106", "ABC-107", "ABC-108", "ABC-109", "ABC-110");
        Pagina<String> pagina = Pagina.de(new PageImpl<>(placas, PageRequest.of(2, 10), 80));

        assertEquals(10, pagina.getContenido().size());
        assertEquals(2, pagina.getPagina());
        assertEquals(10, pagina.getTamano());
        assertEquals(80, pagina.getTotalElementos());
        assertEquals(8, pagina.getTotalPaginas());
    }

    @Test
    @DisplayName("El tamano de pagina se limita a 50 y la pagina no puede ser negativa")
    void solicitudFueraDeRango() {
        Pageable solicitud = Pagina.solicitud(-3, 500, Sort.by("placa"));

        assertEquals(0, solicitud.getPageNumber());
        assertEquals(Pagina.TAMANO_MAXIMO, solicitud.getPageSize());
        assertEquals(Sort.by("placa"), solicitud.getSort());
    }
}
