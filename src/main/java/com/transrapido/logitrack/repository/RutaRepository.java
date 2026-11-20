package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Ruta;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByEstado(EstadoRuta estado);
    Optional<Ruta> findFirstByConductor_IdAndEstado(Long conductorId, EstadoRuta estado);
}
