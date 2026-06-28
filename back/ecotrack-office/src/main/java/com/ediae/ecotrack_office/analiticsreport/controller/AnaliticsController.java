package com.ediae.ecotrack_office.analiticsreport.controller;

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

import com.ediae.ecotrack_office.analiticsreport.dto.AnaliticsReportGenerateDto;
import com.ediae.ecotrack_office.analiticsreport.dto.AnaliticsReportRequestDto;
import com.ediae.ecotrack_office.analiticsreport.model.AnaliticsReportModel;
import com.ediae.ecotrack_office.analiticsreport.service.AnaliticsService;

@RestController
@RequestMapping("/api/v1/analytics-reports") // Ruta unificada para la API de informes
public class AnaliticsController {

    private final AnaliticsService analiticsService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AnaliticsController.class);

    // private final Authentication authentication;

    // Constructor tradicional para inyectar el servicio
    public AnaliticsController(AnaliticsService analiticsService) {
        this.analiticsService = analiticsService;
    }

    // 1. ENDPOINT PARA LISTAR TODOS LOS REPORTES (GET)
    @GetMapping
    public ResponseEntity<List<AnaliticsReportModel>> getAllReports() {
        List<AnaliticsReportModel> lista = analiticsService.getAllReports();
        return ResponseEntity.ok(lista); // Devuelve un 200 OK con la lista
    }

    // 2. ENDPOINT PARA BUSCAR UN REPORTE POR ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<AnaliticsReportModel> getReportById(@PathVariable Long id) {
        AnaliticsReportModel reporte = analiticsService.getReportById(id);
        return ResponseEntity.ok(reporte); // Devuelve un 200 OK con el reporte encontrado
    }

    // 3. ENDPOINT PARA CREAR UN NUEVO REPORTE (POST)
    @PostMapping
    public ResponseEntity<AnaliticsReportModel> createReport(@RequestBody AnaliticsReportRequestDto dto) {
        AnaliticsReportModel nuevoReporte = analiticsService.createReport(dto);
        return ResponseEntity.ok(nuevoReporte); // Devuelve un 200 OK con el reporte creado
    }

    // 3. ENDPOINT PARA CREAR UN NUEVO REPORTE (POST)
    @PostMapping("/generate")
    public ResponseEntity<AnaliticsReportModel> generateReport(Authentication auth,@RequestBody AnaliticsReportGenerateDto dto) {

        logger.info("Creando un nuevo reporte de analítica para la organización del usuario autenticado.");


        Long userId = (Long) auth.getPrincipal()
;
        AnaliticsReportModel nuevoReporte = analiticsService.generateReport(userId, dto);
        return ResponseEntity.ok(nuevoReporte); // Devuelve un 200 OK con el reporte creado
    }

    // 4. ENDPOINT PARA EDITAR UN REPORTE EXISTENTE (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<AnaliticsReportModel> editReport(@PathVariable Long id, @RequestBody AnaliticsReportRequestDto dto) {
        AnaliticsReportModel reporteEditado = analiticsService.editReport(id, dto);
        return ResponseEntity.ok(reporteEditado); // Devuelve un 200 OK con los cambios guardados
    }

    // 5. ENDPOINT PARA ELIMINAR UN REPORTE (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        analiticsService.deleteReport(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 (No Content), que es el estándar para borrados exitosos
    }
}