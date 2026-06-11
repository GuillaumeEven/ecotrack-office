package com.ediae.ecotrack_office.analiticsreport.repository;

import com.ediae.ecotrack_office.analiticsreport.entity.AnaliticsReportEntity; // ¡CORREGIDO: Añadido el .entity.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnaliticsReportRepository extends JpaRepository<AnaliticsReportEntity, Long> {
    List<AnaliticsReportEntity> findByOrganizationId(Long organizationId);
}