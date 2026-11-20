package com.transrapido.logitrack.dto;

// Formato unico de error del backend (Tabla 38 y seccion 5.5): siempre
// {"mensaje": "..."} para que Angular lo muestre igual en toda la aplicacion.
public class ErrorResponse {

    private String mensaje;

    public ErrorResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
