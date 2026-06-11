package com.ediae.ecotrack_office.incident.service;

import com.ediae.ecotrack_office.incident.dto.IncidentRequestDto;
import com.ediae.ecotrack_office.incident.dto.IncidentResponseDto;
import com.ediae.ecotrack_office.incident.model.IncidentEntity;
import com.ediae.ecotrack_office.incident.model.IncidentModel;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import com.ediae.ecotrack_office.incident.repository.IncidentRepository;
import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    // Constructor limpio para inyectar la dependencia del repositorio
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // 1. OBTENER TODAS LAS INCIDENCIAS (GET con bucle tradicional)
    public List<IncidentResponseDto> getAllIncidents() {
        List<IncidentEntity> listaEntidades = incidentRepository.findAll();
        List<IncidentResponseDto> listaDtos = new ArrayList<>();
        
        // Recorro las entidades una a una pasándolas por el mapeador
        for (IncidentEntity entidad : listaEntidades) {
            IncidentModel modelo = IncidentMapper.toModel(entidad);
            IncidentResponseDto dto = IncidentMapper.toResponseDto(modelo);
            listaDtos.add(dto);
        }
        
        return listaDtos;
    }

    // 2. CREAR NUEVA INCIDENCIA (POST con mapeo manual)
    public IncidentResponseDto createIncident(IncidentRequestDto requestDto) {
        // Convertimos el sobre de entrada a nuestro modelo y luego a entidad
        IncidentModel model = IncidentMapper.requestToModel(requestDto);
        IncidentEntity entity = IncidentMapper.toEntity(model);
        
        // CONEXIÓN SIMPLE: Creo los objetos de relación con los IDs del DTO, sin necesidad de consultar la base de datos para obtener las entidades completas
        UserEntity usuario = new UserEntity();
        usuario.setId(requestDto.userId());
        entity.setUser(usuario);
        
        ResourceEntity recurso = new ResourceEntity();
        recurso.setId(requestDto.resourceId());
        entity.setResource(recurso);
        
        // Guardo en la base de datos
        IncidentEntity savedEntity = incidentRepository.save(entity);
        
        // Mapeo de vuelta para devolver el DTO de respuesta
        IncidentModel modeloGuardado = IncidentMapper.toModel(savedEntity);
        return IncidentMapper.toResponseDto(modeloGuardado);
    }

    // 3. RESOLVER INCIDENCIA (PATCH con mapeo manual y actualización de campos específicos)
    public IncidentResponseDto resolveIncident(Long id) {
        Optional<IncidentEntity> resultado = incidentRepository.findById(id);
        
        if (resultado.isEmpty()) {
            throw new RuntimeException("Incidencia no encontrada con ID: " + id);
        }
        
        IncidentEntity entity = resultado.get();
        
        // Modificamos los valores de cierre
        entity.setStatus(IncidentStatus.RESOLVED);
        entity.setResolvedAt(LocalDateTime.now());
        
        IncidentEntity updatedEntity = incidentRepository.save(entity);
        
        IncidentModel modelo = IncidentMapper.toModel(updatedEntity);
        return IncidentMapper.toResponseDto(modelo);
    }

    // 4. ELIMINAR INCIDENCIA (DELETE con comprobación previa)
    public void deleteIncident(Long id) {
        Optional<IncidentEntity> resultado = incidentRepository.findById(id);
        
        if (resultado.isEmpty()) {
            throw new RuntimeException("Incidencia no encontrada con ID: " + id);
        }
        
        incidentRepository.deleteById(id);
    }

    // 5. ACTUALIZAR / EDITAR INCIDENCIA (PUT tradicional)
    public IncidentResponseDto updateIncident(Long id, IncidentRequestDto requestDto) {
        Optional<IncidentEntity> resultado = incidentRepository.findById(id);
        
        if (resultado.isEmpty()) {
            throw new RuntimeException("Incidencia no encontrada con ID: " + id);
        }
        
        IncidentEntity entity = resultado.get();
        
        // Modifico el texto plano
        entity.setDescription(requestDto.description());
        
        // Asocio usuario y recurso por su ID.
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