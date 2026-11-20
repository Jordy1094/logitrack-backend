package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    boolean existsByDni(String dni);
    Optional<Conductor> findByUsuario_Correo(String correo);
}
