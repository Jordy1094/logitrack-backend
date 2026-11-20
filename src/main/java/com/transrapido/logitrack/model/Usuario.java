package com.transrapido.logitrack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transrapido.logitrack.model.enums.RolUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String correo;

    // Nunca debe salir en una respuesta JSON (ni siquiera el hash): RNF-03.
    @JsonIgnore
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    // Un conductor queda vinculado a su cuenta de acceso (1 a 1 opcional, seccion 5.1).
    // @JsonIgnore de este lado porque Conductor ya expone "usuario"; sin esto, Jackson
    // entra en un ciclo infinito conductor -> usuario -> conductor -> usuario...
    @JsonIgnore
    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Conductor conductor;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String contrasena, RolUsuario rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }
}
