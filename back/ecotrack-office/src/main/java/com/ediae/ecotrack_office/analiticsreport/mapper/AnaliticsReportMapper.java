package com.ediae.ecotrack_office.analiticsreport.mapper;

import com.ediae.ecotrack_office.analiticsreport.dto.AnaliticsReportRequestDto;
import com.ediae.ecotrack_office.analiticsreport.dto.AnaliticsReportResponseDto;
import com.ediae.ecotrack_office.analiticsreport.entity.AnaliticsReportEntity;
import com.ediae.ecotrack_office.analiticsreport.model.AnaliticsReportModel;
import org.springframework.stereotype.Component;

@Component
public class AnaliticsReportMapper {

    // 1. De Entidad a Modelo (Para la lógica interna de la aplicación)
    public AnaliticsReportModel toModel(AnaliticsReportEntity entity) {
        if (entity == null) {
            return null;
        }
        AnaliticsReportModel model = new AnaliticsReportModel();
        model.setId(entity.getId());
        model.setOrganizationId(entity.getOrganization().getId());
        model.setCreatedAt(entity.getGeneratedAt()); // ¡CORREGIDO CON TU MÉTODO REAL!
        return model;
    }

    // 2. De RequestDTO a Entidad (Para los POST de creación)
    public AnaliticsReportEntity toEntity(AnaliticsReportRequestDto dto) {
        if (dto == null) {
            return null;
        }
        AnaliticsReportEntity entity = new AnaliticsReportEntity();
        entity.setCo2SavingsKg(dto.co2SavingsKg());
        entity.setEnergySavingsEuros(dto.energySavingsEuros());
        entity.setTotalReservations(dto.totalReservations());
        entity.setConfirmedCheckIns(dto.confirmedCheckIns());
        entity.setEmptyRooms(dto.emptyRooms());
        // La fecha de generación se establece automáticamente al momento de crear la entidad
        entity.setGeneratedAt(java.time.LocalDateTime.now()); 
        return entity;
    }

    // 3. De Modelo a ResponseDTO (Para las respuestas de la API)
    public AnaliticsReportResponseDto toResponseDto(AnaliticsReportEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AnaliticsReportResponseDto(
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