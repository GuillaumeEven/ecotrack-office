package com.ediae.ecotrack_office.incident.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.service.IncidentService;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // GET: Obtener el listado completo
    @GetMapping
    public ResponseEntity<List<IncidentResponseDto>> getAll(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(incidentService.getAllIncidents(userId));
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