package com.ediae.ecotrack_office.incident.controller;

import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.service.IncidentService;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private IncidentMapper incidentMapper;

    @GetMapping
    public ResponseEntity<List<IncidentResponseDto>> getAllIncidents() {
        List<IncidentResponseDto> response = incidentService.getAllIncidents().stream()
                .map(incidentMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}