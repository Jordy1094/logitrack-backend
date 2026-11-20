package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CamionRepository extends JpaRepository<Camion, Long> {
    boolean existsByPlaca(String placa);
    List<Camion> findByEstado(EstadoCamion estado);
    Optional<Camion> findByPlaca(String placa);
}
