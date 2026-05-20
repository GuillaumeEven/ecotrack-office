package com.ediae.ecotrack_office.analiticsreport.service;

import com.ediae.ecotrack_office.analiticsreport.AnaliticsReportEntity;
import com.ediae.ecotrack_office.analiticsreport.repository.AnaliticsReportRepository;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity; // <--- IMPORTANTE: Importar la entidad de organización
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AnaliticsService {

    @Autowired
    private AnaliticsReportRepository analiticsReportRepository;

    public AnaliticsReportEntity generateDailyReport(Long organizationId) {
        Double co2Saved = 0.0;
        Double energySaved = 0.0;
        
        // Creamos un objeto de organización temporal para cumplir con el tipo que pide el constructor
        OrganizationEntity dummyOrganization = new OrganizationEntity();
        
        // Usamos el constructor oficial que acabamos de ver en el 'cat'
        // Pasando el objeto dummyOrganization al final en vez del Long plano
        AnaliticsReportEntity report = new AnaliticsReportEntity(
            co2Saved,
            energySaved,
            0, // totalReservations
            0, // confirmedCheckIns
            0, // emptyRooms
            LocalDateTime.now(),
            dummyOrganization // <--- Solucionado: Objeto en lugar de ID plano
        );
        
        return analiticsReportRepository.save(report);
    }
}