package com.transrapido.logitrack.dto;

import com.transrapido.logitrack.model.enums.RolUsuario;

public class LoginResponse {

    private String token;
    private RolUsuario rol;
    private String nombre;

    public LoginResponse(String token, RolUsuario rol, String nombre) {
        this.token = token;
        this.rol = rol;
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }
}
