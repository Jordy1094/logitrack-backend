package com.transrapido.logitrack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "camion")
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 7)
    private String placa;

    private String marca;
    private String modelo;
    private Integer anio;
    private Double capacidad;

    @Enumerated(EnumType.STRING)
    private EstadoCamion estado;

    // Ultima novedad reportada por el conductor (RF-16, POST /api/camiones/{id}/reportes).
    // No es una entidad aparte: el Dashboard solo necesita el ultimo estado reportado.
    private boolean conNovedad = false;
    private String descripcionNovedad;

    // @JsonIgnore: Ruta.camion y Mantenimiento.camion ya muestran el camion desde el otro
    // lado (Tabla 35); sin esto, Jackson entra en un ciclo camion -> rutas -> camion -> ...
    @JsonIgnore
    @OneToMany(mappedBy = "camion", fetch = FetchType.LAZY)
    private List<Ruta> rutas = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "camion", fetch = FetchType.LAZY)
    private List<Mantenimiento> mantenimientos = new ArrayList<>();

    public Camion() {
    }

    public Camion(String placa, String marca, String modelo, Integer anio, Double capacidad) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.capacidad = capacidad;
        this.estado = EstadoCamion.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoCamion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCamion estado) {
        this.estado = estado;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }

    public List<Mantenimiento> getMantenimientos() {
        return mantenimientos;
    }

    public boolean isConNovedad() {
        return conNovedad;
    }

    public void setConNovedad(boolean conNovedad) {
        this.conNovedad = conNovedad;
    }

    public String getDescripcionNovedad() {
        return descripcionNovedad;
    }

    public void setDescripcionNovedad(String descripcionNovedad) {
        this.descripcionNovedad = descripcionNovedad;
    }
    
}
