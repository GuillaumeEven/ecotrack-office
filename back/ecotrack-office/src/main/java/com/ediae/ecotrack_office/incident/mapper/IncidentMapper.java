package com.ediae.ecotrack_office.incident.mapper;

import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.model.IncidentEntity;
import com.ediae.ecotrack_office.incident.model.IncidentModel;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentModel toModel(IncidentEntity entity) {
        if (entity == null) return null;
        IncidentModel model = new IncidentModel();
        model.setId(entity.getId());
        model.setDescription(entity.getDescription());
        model.setStatus(entity.getStatus());
        model.setCreatedAt(entity.getCreatedAt());
        model.setResolvedAt(entity.getResolvedAt());
        if (entity.getUser() != null) model.setUserId(entity.getUser().getId());
        if (entity.getResource() != null) model.setResourceId(entity.getResource().getId());
        return model;
    }

    public IncidentResponseDto toResponseDto(IncidentModel model) {
        if (model == null) return null;
        
        // Al ser un 'record', usamos su constructor pasándole todos los parámetros en orden
        return new IncidentResponseDto(
            model.getId(),
            model.getDescription(),
            model.getStatus(),
            model.getCreatedAt(),
            model.getResolvedAt(),
            model.getUserId(),
            model.getResourceId()
        );
    }
}