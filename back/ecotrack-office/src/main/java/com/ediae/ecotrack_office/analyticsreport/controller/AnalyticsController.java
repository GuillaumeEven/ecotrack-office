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

    // private final Authentication authentication;

    // Constructor tradicional para inyectar el servicio
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // 1. ENDPOINT PARA LISTAR TODOS LOS REPORTES (GET)
    @GetMapping
    public ResponseEntity<List<AnalyticsReportModel>> getAllReports() {
        List<AnalyticsReportModel> lista = analyticsService.getAllReports();
        return ResponseEntity.ok(lista); // Devuelve un 200 OK con la lista
    }

    // 2. ENDPOINT PARA BUSCAR UN REPORTE POR ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<AnalyticsReportModel> getReportById(@PathVariable Long id) {
        AnalyticsReportModel reporte = analyticsService.getReportById(id);
        return ResponseEntity.ok(reporte); // Devuelve un 200 OK con el reporte encontrado
    }

    // 3. ENDPOINT PARA CREAR UN NUEVO REPORTE (POST)
    @PostMapping
    public ResponseEntity<AnalyticsReportModel> createReport(@RequestBody AnalyticsReportRequestDto dto) {
        AnalyticsReportModel nuevoReporte = analyticsService.createReport(dto);
        return ResponseEntity.ok(nuevoReporte); // Devuelve un 200 OK con el reporte creado
    }

    // 3. ENDPOINT PARA CREAR UN NUEVO REPORTE (POST)
    @PostMapping("/generate")
    public ResponseEntity<AnalyticsReportModel> generateReport(Authentication auth,@RequestBody AnalyticsReportGenerateDto dto) {

        Long userId = (Long) auth.getPrincipal()
;
        AnalyticsReportModel nuevoReporte = analyticsService.generateReport(userId, dto);
        return ResponseEntity.ok(nuevoReporte); // Devuelve un 200 OK con el reporte creado
    }

    // 4. ENDPOINT PARA EDITAR UN REPORTE EXISTENTE (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<AnalyticsReportModel> editReport(@PathVariable Long id, @RequestBody AnalyticsReportRequestDto dto) {
        AnalyticsReportModel reporteEditado = analyticsService.editReport(id, dto);
        return ResponseEntity.ok(reporteEditado); // Devuelve un 200 OK con los cambios guardados
    }

    // 5. ENDPOINT PARA ELIMINAR UN REPORTE (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        analyticsService.deleteReport(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 (No Content), que es el estándar para borrados exitosos
    }
}