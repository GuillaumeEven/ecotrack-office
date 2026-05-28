package com.ediae.ecotrack_office.incident.controller;

import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.service.IncidentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // GET: Obtener el listado completo
    @GetMapping
    public ResponseEntity<List<IncidentResponseDto>> getAll() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    // POST: Crear una nueva incidencia
    @PostMapping
    public ResponseEntity<IncidentResponseDto> create(@RequestBody IncidentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.createIncident(requestDto));
    }

    // PATCH: Cerrar una incidencia existente
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponseDto> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.resolveIncident(id));
    }

    // DELETE: Borrar una incidencia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}