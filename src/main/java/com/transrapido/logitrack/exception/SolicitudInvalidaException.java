package com.transrapido.logitrack.exception;

// Dato duplicado o invalido que la plantilla documenta como 400: placa o DNI ya
// registrados (Tabla 34), o el motivo/taller vacio al programar un mantenimiento.
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
