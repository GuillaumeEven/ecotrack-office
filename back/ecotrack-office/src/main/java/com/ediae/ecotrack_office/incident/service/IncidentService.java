package com.ediae.ecotrack_office.incident.service;

import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.model.IncidentEntity;
import com.ediae.ecotrack_office.incident.model.IncidentModel;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import com.ediae.ecotrack_office.incident.repository.IncidentRepository;
import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // 1. OBTENER TODAS 
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(IncidentMapper::toModel)
                .map(IncidentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // 2. CREAR NUEVA INCIDENCIA (POST)
    @Transactional
    public IncidentResponseDto createIncident(IncidentRequestDto requestDto) {
        // Traducimos el sobre de entrada (Record) a Modelo de negocio
        IncidentModel model = IncidentMapper.requestToModel(requestDto);
        
        // Lo pasamos a Entidad de base de datos
        IncidentEntity entity = IncidentMapper.toEntity(model);
        
        // Cargamos las referencias de Hibernate usando los IDs planos de forma ligera
        entity.setUser(entityManager.getReference(UserEntity.class, requestDto.userId()));
        entity.setResource(entityManager.getReference(ResourceEntity.class, requestDto.resourceId()));
        
        // Guardamos de forma oficial
        IncidentEntity savedEntity = incidentRepository.save(entity);
        return IncidentMapper.toResponseDto(IncidentMapper.toModel(savedEntity));
    }

    // 3. RESOLVER INCIDENCIA (PATCH)
    @Transactional
    public IncidentResponseDto resolveIncident(Long id) {
        IncidentEntity entity = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada con ID: " + id));
        
        // Aplicamos los cambios de estado y cierre
        entity.setStatus(IncidentStatus.RESOLVED);
        entity.setResolvedAt(LocalDateTime.now());
        
        IncidentEntity updatedEntity = incidentRepository.save(entity);
        return IncidentMapper.toResponseDto(IncidentMapper.toModel(updatedEntity));
    }

    // 4. ELIMINAR INCIDENCIA (DELETE)
    @Transactional
    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incidencia no encontrada con ID: " + id);
        }
        incidentRepository.deleteById(id);
    }
}