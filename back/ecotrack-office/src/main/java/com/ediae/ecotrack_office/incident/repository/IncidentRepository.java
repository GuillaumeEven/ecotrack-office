package com.ediae.ecotrack_office.incident.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.incident.entity.IncidentEntity;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {
    // Buscar incidencias de un usuario concreto
    List<IncidentEntity> findByUserId(Long userId);

    // Buscar incidencias por su estado (OPEN, CLOSED)
    List<IncidentEntity> findByStatus(String status);

    // Buscar incidencias por el id del recurso asociado
    List<IncidentEntity> findByResourceId(Long resourceId);
}