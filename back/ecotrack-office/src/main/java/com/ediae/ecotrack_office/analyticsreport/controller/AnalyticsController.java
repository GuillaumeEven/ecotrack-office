package com.ediae.ecotrack_office.analyticsreport.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.analyticsreport.dto.AnalyticsReportGenerateDto;
import com.ediae.ecotrack_office.analyticsreport.dto.AnalyticsReportRequestDto;
import com.ediae.ecotrack_office.analyticsreport.model.AnalyticsReportModel;
import com.ediae.ecotrack_office.analyticsreport.service.AnalyticsService;

@RestController
@RequestMapping("/api/v1/analytics-reports") // Ruta unificada para la API de informes
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsReportModel>> getAllReports(Authentication auth) {

        Long userId = (Long) auth.getPrincipal();

        List<AnalyticsReportModel> lista = analyticsService.getAllReports(userId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsReportModel> getReportById(@PathVariable Long id) {
        AnalyticsReportModel reporte = analyticsService.getReportById(id);
        return ResponseEntity.ok(reporte);
    }

    // Endpoint para crear un nuevo reporte hardcodeado
    @PostMapping
    public ResponseEntity<AnalyticsReportModel> createReport(@RequestBody AnalyticsReportRequestDto dto) {
        AnalyticsReportModel nuevoReporte = analyticsService.createReport(dto);
        return ResponseEntity.ok(nuevoReporte);
    }

    // Endpoint para crear un nuevo reporte según las reservas del día
    @PostMapping("/generate")
    public ResponseEntity<AnalyticsReportModel> generateReport(Authentication auth,@RequestBody AnalyticsReportGenerateDto dto) {

        Long userId = (Long) auth.getPrincipal();
        AnalyticsReportModel nuevoReporte = analyticsService.generateReport(userId, dto);
        return ResponseEntity.ok(nuevoReporte);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsReportModel> editReport(@PathVariable Long id, @RequestBody AnalyticsReportRequestDto dto) {
        AnalyticsReportModel reporteEditado = analyticsService.editReport(id, dto);
        return ResponseEntity.ok(reporteEditado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        analyticsService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}