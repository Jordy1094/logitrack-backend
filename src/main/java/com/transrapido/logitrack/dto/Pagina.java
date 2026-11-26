package com.transrapido.logitrack.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

// RNF-17: respuesta de las listas que se cargan por paginas (camiones, conductores y rutas).
// Se usa un DTO propio en lugar de serializar Page de Spring, para que el JSON sea estable
// y tenga los nombres del dominio: contenido, pagina, tamano, totalElementos, totalPaginas.
public class Pagina<T> {

    // Limites del tamano de pagina: evita que un cliente pida toda la tabla de una vez.
    public static final int TAMANO_MAXIMO = 50;

    private final List<T> contenido;
    private final int pagina;
    private final int tamano;
    private final long totalElementos;
    private final int totalPaginas;

    public Pagina(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
        this.contenido = contenido;
        this.pagina = pagina;
        this.tamano = tamano;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
    }

    public static <T> Pagina<T> de(Page<T> page) {
        return new Pagina<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    // Normaliza lo que pide el cliente: la pagina empieza en 0 y el tamano va de 1 a 50.
    public static Pageable solicitud(int pagina, int tamano, Sort orden) {
        int paginaValida = Math.max(pagina, 0);
        int tamanoValido = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO);
        return PageRequest.of(paginaValida, tamanoValido, orden);
    }

    public List<T> getContenido() {
        return contenido;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamano() {
        return tamano;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}
