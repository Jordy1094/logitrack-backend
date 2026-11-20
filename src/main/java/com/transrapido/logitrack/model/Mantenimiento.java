package com.transrapido.logitrack.model;

import com.transrapido.logitrack.model.enums.EstadoMantenimiento;
import com.transrapido.logitrack.model.enums.TipoMantenimiento;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoMantenimiento tipo;

    private LocalDate fechaProgramada;
    private String motivo;
    private String taller;
    private LocalDate fechaIngreso;
    private LocalDate fechaCierre;

    @Enumerated(EnumType.STRING)
    private EstadoMantenimiento estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camion_id", nullable = false)
    private Camion camion;

    public Mantenimiento() {
    }

    public Mantenimiento(Camion camion, TipoMantenimiento tipo, LocalDate fechaProgramada, String motivo, String taller) {
        this.camion = camion;
        this.tipo = tipo;
        this.fechaProgramada = fechaProgramada;
        this.motivo = motivo;
        this.taller = taller;
        this.estado = EstadoMantenimiento.PROGRAMADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTaller() {
        return taller;
    }

    public void setTaller(String taller) {
        this.taller = taller;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public EstadoMantenimiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoMantenimiento estado) {
        this.estado = estado;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }
}
