package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Camion;
import com.transrapido.logitrack.model.enums.EstadoCamion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CamionRepository extends JpaRepository<Camion, Long> {
    boolean existsByPlaca(String placa);
    List<Camion> findByEstado(EstadoCamion estado);
    Optional<Camion> findByPlaca(String placa);

    // RNF-17: lista por paginas con buscador por placa (parcial) y filtro por estado (RF-05).
    Page<Camion> findByPlacaContainingIgnoreCase(String placa, Pageable pageable);
    Page<Camion> findByEstadoAndPlacaContainingIgnoreCase(EstadoCamion estado, String placa, Pageable pageable);
}
