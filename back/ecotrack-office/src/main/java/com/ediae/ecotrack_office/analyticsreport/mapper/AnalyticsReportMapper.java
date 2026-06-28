package com.ediae.ecotrack_office.analyticsreport.mapper;

import com.ediae.ecotrack_office.analyticsreport.dto.AnalyticsReportRequestDto;
import com.ediae.ecotrack_office.analyticsreport.dto.AnalyticsReportResponseDto;
import com.ediae.ecotrack_office.analyticsreport.entity.AnalyticsReportEntity;
import com.ediae.ecotrack_office.analyticsreport.model.AnalyticsReportModel;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsReportMapper {

    // 1. De Entidad a Modelo (Para la lógica interna de la aplicación)
    // Ahora sí se copian todas las métricas reales para que viajen al frontend
    public AnalyticsReportModel toModel(AnalyticsReportEntity entity) {
        if (entity == null) {
            return null;
        }
        AnalyticsReportModel model = new AnalyticsReportModel();
        model.setId(entity.getId());
        model.setOrganizationId(entity.getOrganization().getId());
        model.setCreatedAt(entity.getGeneratedAt());

        // Métricas inyectadas al modelo (para que el frontend las reciba correctamente)
        model.setCo2Saved(entity.getCo2SavingsKg()); // Lo añado para que el modelo tenga la métrica de CO2 en kg
        model.setCo2SavingsKg(entity.getCo2SavingsKg());
        model.setEnergySavingsEuros(entity.getEnergySavingsEuros());
        model.setTotalReservations(entity.getTotalReservations());
        model.setConfirmedCheckIns(entity.getConfirmedCheckIns());
        model.setEmptyRooms(entity.getEmptyRooms());

        return model;
    }

    // 2. De RequestDTO a Entidad (Para los POST de creación)
    public AnalyticsReportEntity toEntity(AnalyticsReportRequestDto dto) {
        if (dto == null) {
            return null;
        }
        AnalyticsReportEntity entity = new AnalyticsReportEntity();
        entity.setCo2SavingsKg(dto.co2SavingsKg());
        entity.setEnergySavingsEuros(dto.energySavingsEuros());
        entity.setTotalReservations(dto.totalReservations());
        entity.setConfirmedCheckIns(dto.confirmedCheckIns());
        entity.setEmptyRooms(dto.emptyRooms());
        entity.setGeneratedAt(java.time.LocalDateTime.now());
        return entity;
    }

    // 3. De Modelo a ResponseDTO (Para las respuestas de la API)
    public AnalyticsReportResponseDto toResponseDto(AnalyticsReportEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AnalyticsReportResponseDto(
            entity.getId(),
            entity.getCo2SavingsKg(),
            entity.getEnergySavingsEuros(),
            entity.getTotalReservations(),
            entity.getConfirmedCheckIns(),
            entity.getEmptyRooms(),
            entity.getGeneratedAt(),
            entity.getOrganization().getId()
        );
    }
}