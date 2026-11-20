package com.transrapido.logitrack.dto;

import jakarta.validation.constraints.NotNull;

public class ReporteRequest {

    @NotNull(message = "conNovedad es obligatorio")
    private Boolean conNovedad;

    private String descripcion;

    public Boolean getConNovedad() {
        return conNovedad;
    }

    public void setConNovedad(Boolean conNovedad) {
        this.conNovedad = conNovedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
