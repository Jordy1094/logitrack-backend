package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    boolean existsByDni(String dni);
    Optional<Conductor> findByUsuario_Correo(String correo);

    // RNF-17: lista por paginas con buscador por nombre, apellido o DNI.
    @Query(value = "select c from Conductor c where lower(concat(c.nombres, ' ', c.apellidos, ' ', c.dni)) like lower(concat('%', :texto, '%'))",
           countQuery = "select count(c) from Conductor c where lower(concat(c.nombres, ' ', c.apellidos, ' ', c.dni)) like lower(concat('%', :texto, '%'))")
    Page<Conductor> buscar(@Param("texto") String texto, Pageable pageable);

    // Igual que buscar, pero solo los conductores sin una ruta en curso (RF-06).
    @Query(value = "select c from Conductor c where lower(concat(c.nombres, ' ', c.apellidos, ' ', c.dni)) like lower(concat('%', :texto, '%'))"
                 + " and not exists (select r from Ruta r where r.conductor = c and r.estado = :enCurso)",
           countQuery = "select count(c) from Conductor c where lower(concat(c.nombres, ' ', c.apellidos, ' ', c.dni)) like lower(concat('%', :texto, '%'))"
                 + " and not exists (select r from Ruta r where r.conductor = c and r.estado = :enCurso)")
    Page<Conductor> buscarDisponibles(@Param("texto") String texto, @Param("enCurso") EstadoRuta enCurso, Pageable pageable);
}
