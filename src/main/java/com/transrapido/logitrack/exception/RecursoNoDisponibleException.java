package com.transrapido.logitrack.exception;

// Se lanza cuando un camion o un conductor ya no estan disponibles al guardar,
// o una ruta/mantenimiento ya estaba cerrado (Tabla 38, "Recurso no disponible", 409).
public class RecursoNoDisponibleException extends RuntimeException {
    public RecursoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
