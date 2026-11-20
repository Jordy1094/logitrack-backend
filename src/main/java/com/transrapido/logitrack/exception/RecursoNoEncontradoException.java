package com.transrapido.logitrack.exception;

// Se lanza cuando el id de la ruta no corresponde a ningun camion, conductor,
// ruta o mantenimiento existente (Tabla 38, "Recurso no encontrado", 404).
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
