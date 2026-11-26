package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Ruta;
import com.transrapido.logitrack.model.enums.EstadoRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByEstado(EstadoRuta estado);

    List<Ruta> findByEstadoOrderByFechaSalidaDesc(EstadoRuta estado);

    List<Ruta> findAllByOrderByFechaSalidaDesc();
    Optional<Ruta> findFirstByConductor_IdAndEstado(Long conductorId, EstadoRuta estado);

    // RNF-17: rutas por paginas, filtradas por estado.
    Page<Ruta> findByEstado(EstadoRuta estado, Pageable pageable);
}
