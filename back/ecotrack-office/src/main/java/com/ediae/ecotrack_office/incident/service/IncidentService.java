package com.ediae.ecotrack_office.incident.service;

import com.ediae.ecotrack_office.incident.model.IncidentModel;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import com.ediae.ecotrack_office.incident.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentMapper incidentMapper;

    public List<IncidentModel> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(incidentMapper::toModel)
                .collect(Collectors.toList());
    }
}