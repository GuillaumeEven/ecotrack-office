package com.ediae.ecotrack_office.incident.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.incident.entity.IncidentEntity;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {
    
    List<IncidentEntity> findByUserId(Long userId);

    List<IncidentEntity> findByStatus(String status);

    List<IncidentEntity> findByResourceId(Long resourceId);

    List<IncidentEntity> findByUserOrganizationId(Long organizationId);
}