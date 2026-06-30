package com.ediae.ecotrack_office.incident.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.entity.IncidentEntity;
import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import com.ediae.ecotrack_office.incident.model.IncidentModel;
import com.ediae.ecotrack_office.incident.repository.IncidentRepository;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.repository.UserRepository;

@Service
public class IncidentService {

    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository, UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    public List<IncidentResponseDto> getAllIncidents(Long userId) {
        Long organizationId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId))
                .getOrganization()
                .getId();

        List<IncidentEntity> listaEntidades = incidentRepository.findByUserOrganizationId(organizationId);
        List<IncidentResponseDto> listaDtos = new ArrayList<>();

        for (IncidentEntity entidad : listaEntidades) {
            IncidentModel modelo = IncidentMapper.toModel(entidad);
            IncidentResponseDto dto = IncidentMapper.toResponseDto(modelo);
            listaDtos.add(dto);
        }

        return listaDtos;
    }

    public IncidentResponseDto createIncident(IncidentRequestDto requestDto) {

        IncidentModel model = IncidentMapper.requestToModel(requestDto);
        IncidentEntity entity = IncidentMapper.toEntity(model);

        // Usamos la conexión simple
        UserEntity usuario = new UserEntity();
        usuario.setId(requestDto.userId());
        entity.setUser(usuario);

        ResourceEntity recurso = new ResourceEntity();
        recurso.setId(requestDto.resourceId());
        entity.setResource(recurso);

        IncidentEntity savedEntity = incidentRepository.save(entity);

        IncidentModel modeloGuardado = IncidentMapper.toModel(savedEntity);
        return IncidentMapper.toResponseDto(modeloGuardado);
    }

    public IncidentResponseDto resolveIncident(Long id) {
        Optional<IncidentEntity> resultado = incidentRepository.findById(id);

        if (resultado.isEmpty()) {
            throw new NotFoundException("Incidencia no encontrada con ID: " + id);
        }

        IncidentEntity entity = resultado.get();

        entity.setStatus(IncidentStatus.RESOLVED);
        entity.setResolvedAt(LocalDateTime.now());

        IncidentEntity updatedEntity = incidentRepository.save(entity);

        IncidentModel modelo = IncidentMapper.toModel(updatedEntity);
        return IncidentMapper.toResponseDto(modelo);
    }

    public void deleteIncident(Long id) {
        Optional<IncidentEntity> resultado = incidentRepository.findById(id);

        if (resultado.isEmpty()) {
            throw new NotFoundException("Incidencia no encontrada con ID: " + id);
        }

        incidentRepository.deleteById(id);
    }

    public IncidentResponseDto updateIncident(Long id, IncidentRequestDto requestDto) {
        Optional<IncidentEntity> resultado = incidentRepository.findById(id);

        if (resultado.isEmpty()) {
            throw new NotFoundException("Incidencia no encontrada con ID: " + id);
        }

        IncidentEntity entity = resultado.get();

        entity.setDescription(requestDto.description());

        UserEntity usuario = new UserEntity();
        usuario.setId(requestDto.userId());
        entity.setUser(usuario);

        ResourceEntity recurso = new ResourceEntity();
        recurso.setId(requestDto.resourceId());
        entity.setResource(recurso);

        IncidentEntity savedEntity = incidentRepository.save(entity);

        IncidentModel modelo = IncidentMapper.toModel(savedEntity);
        return IncidentMapper.toResponseDto(modelo);
    }
}