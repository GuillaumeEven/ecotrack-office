package com.ediae.ecotrack_office.analyticsreport.repository;

import com.ediae.ecotrack_office.analyticsreport.entity.AnalyticsReportEntity; // ¡CORREGIDO: Añadido el .entity.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalyticsReportRepository extends JpaRepository<AnalyticsReportEntity, Long> {
    List<AnalyticsReportEntity> findByOrganizationId(Long organizationId);
}