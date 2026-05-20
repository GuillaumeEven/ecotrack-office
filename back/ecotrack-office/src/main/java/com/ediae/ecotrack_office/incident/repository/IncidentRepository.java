package com.ediae.ecotrack_office.incident.repository;

import com.ediae.ecotrack_office.incident.model.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {
    // Buscar incidencias de un usuario concreto
    List<IncidentEntity> findByUserId(Long userId);
    
    // Buscar incidencias por su estado (OPEN, CLOSED)
    List<IncidentEntity> findByStatus(String status);
}