package com.transrapido.logitrack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conductor")
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    private String nombres;
    private String apellidos;
    private String nroLicencia;
    private String telefono;

    // @JsonIgnore: es una relacion LAZY; Jackson no puede serializar el proxy de
    // Hibernate cuando no esta inicializado (rompe GET /api/conductores). La cuenta
    // de acceso es un dato interno, no se necesita en la respuesta del listado.
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    // @JsonIgnore: Ruta.conductor ya muestra el conductor desde el otro lado (Tabla 35);
    // sin esto, Jackson entra en un ciclo conductor -> rutas -> conductor -> rutas...
    @JsonIgnore
    @OneToMany(mappedBy = "conductor", fetch = FetchType.LAZY)
    private List<Ruta> rutas = new ArrayList<>();

    public Conductor() {
    }

    public Conductor(String dni, String nombres, String apellidos, String nroLicencia, String telefono) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.nroLicencia = nroLicencia;
        this.telefono = telefono;
    }

    // Regla de negocio usada por RutaService.asignarRuta (seccion 5.1): un conductor
    // no puede tener dos rutas EN_CURSO al mismo tiempo (RF-08, HU-05 - CA2).
    public boolean tieneRutaActiva() {
        return rutas.stream().anyMatch(r -> r.getEstado() == EstadoRuta.EN_CURSO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNroLicencia() {
        return nroLicencia;
    }

    public void setNroLicencia(String nroLicencia) {
        this.nroLicencia = nroLicencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }
}
