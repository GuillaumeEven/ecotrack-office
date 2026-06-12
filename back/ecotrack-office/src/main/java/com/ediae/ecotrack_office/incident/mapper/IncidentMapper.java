package com.ediae.ecotrack_office.incident.mapper;

import java.time.LocalDateTime;

import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.entity.IncidentEntity;
import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import com.ediae.ecotrack_office.incident.model.IncidentModel;

public class IncidentMapper {

    // 1. Traduce de Entidad de Base de Datos a Modelo de Negocio
    public static IncidentModel toModel(IncidentEntity entity) {
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

    // 2. Traduce de Modelo de Negocio al Record DTO de salida (Frontend)
    public static IncidentResponseDto toResponseDto(IncidentModel model) {
        if (model == null) return null;
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

    // 3. Convierte el sobre de entrada (Request) en un Modelo de Negocio
    public static IncidentModel requestToModel(IncidentRequestDto dto) {
        if (dto == null) return null;
        IncidentModel model = new IncidentModel();
        model.setDescription(dto.description());
        model.setResourceId(dto.resourceId());
        model.setUserId(dto.userId());
        model.setStatus(IncidentStatus.IN_PROGRESS);
        model.setCreatedAt(LocalDateTime.now());
        return model;
    }

    // 4. Traduce el Modelo de Negocio a una Entidad limpia para la base de datos
    public static IncidentEntity toEntity(IncidentModel model) {
        if (model == null) return null;
        IncidentEntity entity = new IncidentEntity();
        entity.setId(model.getId());
        entity.setDescription(model.getDescription());
        entity.setStatus(model.getStatus());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setResolvedAt(model.getResolvedAt());
        return entity;
    }
}