package com.ediae.ecotrack_office.incident.controller;

import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.service.IncidentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ediae.ecotrack_office.shared.guard.RoleGuard;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final RoleGuard roleGuard;

    public IncidentController(IncidentService incidentService, RoleGuard roleGuard) {
        this.incidentService = incidentService;
        this.roleGuard = roleGuard;
    }

    // GET: Obtener el listado completo
    @GetMapping
    public ResponseEntity<List<IncidentResponseDto>> getAll(Authentication auth) {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    // POST: Crear una nueva incidencia
    @PostMapping
    public ResponseEntity<IncidentResponseDto> create(Authentication auth, @RequestBody IncidentRequestDto requestDto) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.createIncident(requestDto));
    }

    // PATCH: Cerrar una incidencia existente
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponseDto> resolve(Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(incidentService.resolveIncident(id));
    }

    // DELETE: Borrar una incidencia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}