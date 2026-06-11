package com.ediae.ecotrack_office.analiticsreport.controller;

import com.ediae.ecotrack_office.analiticsreport.dto.AnaliticsReportRequestDto;
import com.ediae.ecotrack_office.analiticsreport.model.AnaliticsReportModel;
import com.ediae.ecotrack_office.analiticsreport.service.AnaliticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics-reports") // Ruta unificada para la API de informes
public class AnaliticsController {

    private final AnaliticsService analiticsService;

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