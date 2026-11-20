package com.transrapido.logitrack.repository;

import com.transrapido.logitrack.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findByCamion_IdOrderByFechaProgramadaDesc(Long camionId);
}
