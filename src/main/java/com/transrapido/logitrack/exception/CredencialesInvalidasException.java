package com.transrapido.logitrack.exception;

// Correo o contrasena incorrectos en el login (Tabla 34: POST /api/auth/login -> 401).
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
