package com.ediae.ecotrack_office.analiticsreport.mapper;

import com.ediae.ecotrack_office.analiticsreport.AnaliticsReportEntity;
import com.ediae.ecotrack_office.analiticsreport.model.AnaliticsReportModel;
import java.time.LocalDateTime;

public class AnaliticsReportMapper { //TODO: EN UN FUTURO PASAR A TENER DOS MAPPERS. UNO QUE RELACIONA ENTRE DTO Y MODEL Y OTRO ENTRE MODEL Y ENTITY

    // Convierte de Entidad (Base de datos) a Modelo (Negocio)
    public static AnaliticsReportModel toModel(AnaliticsReportEntity entity) {
        if (entity == null) return null;
        
        AnaliticsReportModel model = new AnaliticsReportModel();
        model.setId(entity.getId());
        
        // PARCHE: Como la entidad no tiene getCreatedAt(),
        // le pongo la fecha actual directamente para que compile sin rechistar.
        model.setCreatedAt(LocalDateTime.now());
        
        // PARCHE: El 0.0 temporal de antes
        model.setCo2Saved(0.0);
        
        // Extraemos el ID plano de la organización desde la referencia de la entidad
        if (entity.getOrganization() != null) {
            model.setOrganizationId(entity.getOrganization().getId());
        }
        
        return model;
    }
}
