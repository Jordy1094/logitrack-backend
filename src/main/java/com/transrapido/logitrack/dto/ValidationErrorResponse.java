package com.transrapido.logitrack.dto;

import java.util.Map;

// Error 400 de @Valid: devuelve un mapa campo -> mensaje, en el formato que
// Angular usara para marcar cada campo del formulario (seccion 2.3 / 4.3).
public class ValidationErrorResponse {

    private Map<String, String> errores;

    public ValidationErrorResponse(Map<String, String> errores) {
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
