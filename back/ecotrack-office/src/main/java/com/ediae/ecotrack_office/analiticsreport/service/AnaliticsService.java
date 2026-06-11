package com.ediae.ecotrack_office.analiticsreport.service;

import com.ediae.ecotrack_office.analiticsreport.dto.AnaliticsReportRequestDto;
import com.ediae.ecotrack_office.analiticsreport.entity.AnaliticsReportEntity;
import com.ediae.ecotrack_office.analiticsreport.model.AnaliticsReportModel;
import com.ediae.ecotrack_office.analiticsreport.mapper.AnaliticsReportMapper;
import com.ediae.ecotrack_office.analiticsreport.repository.AnaliticsReportRepository;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnaliticsService {

    private final AnaliticsReportRepository analiticsReportRepository;
    private final OrganizationRepository organizationRepository; 
    private final AnaliticsReportMapper analiticsReportMapper;

    // Constructor estándar para inyectar las dependencias
    public AnaliticsService(AnaliticsReportRepository analiticsReportRepository, 
                            OrganizationRepository organizationRepository, 
                            AnaliticsReportMapper analiticsReportMapper) {
        this.analiticsReportRepository = analiticsReportRepository;
        this.organizationRepository = organizationRepository;
        this.analiticsReportMapper = analiticsReportMapper;
    }

    // 1. OBTENER TODOS LOS REPORTES (Bucle tradicional)
    public List<AnaliticsReportModel> getAllReports() {
        List<AnaliticsReportEntity> listaEntidades = analiticsReportRepository.findAll();
        List<AnaliticsReportModel> listaModelos = new ArrayList<>();
        
        // Recorro la lista de la base de datos uno a uno y los convierto a modelos usando el Mapper
        for (AnaliticsReportEntity entidad : listaEntidades) {
            AnaliticsReportModel modelo = analiticsReportMapper.toModel(entidad);
            listaModelos.add(modelo);
        }
        
        return listaModelos;
    }

    // 2. OBTENER UN REPORTE POR SU ID
    public AnaliticsReportModel getReportById(Long id) {
        Optional<AnaliticsReportEntity> resultado = analiticsReportRepository.findById(id);
        
        // Si no existe en la base de datos, lanzo una excepción personalizada de "No encontrado"
        if (resultado.isEmpty()) {
            throw new NotFoundException("Reporte de analítica no encontrado con ID: " + id);
        }
        
        // Si existe, lo saco de la Optional, lo convierto a modelo y lo devuelvo
        AnaliticsReportEntity entidad = resultado.get();
        return analiticsReportMapper.toModel(entidad);
    }

    // 3. CREAR UN NUEVO REPORTE
    public AnaliticsReportModel createReport(AnaliticsReportRequestDto dto) {
        // Busco si existe la organización que manda en el DTO
        Optional<OrganizationEntity> resultadoOrg = organizationRepository.findById(dto.organizationId());
        if (resultadoOrg.isEmpty()) {
            throw new NotFoundException("Organización no encontrada con ID: " + dto.organizationId());
        }
        OrganizationEntity organizacion = resultadoOrg.get();

        // Convierto el DTO a Entidad limpia
        AnaliticsReportEntity entidad = analiticsReportMapper.toEntity(dto);
        
        // Le asigno la organización a la entidad antes de guardarla, porque el Mapper no tiene esa información
        entidad.setOrganization(organizacion);
        
        // Guardo en la base de datos
        AnaliticsReportEntity guardado = analiticsReportRepository.save(entidad);
        
        // Devuelvo el resultado pasado a modelo
        return analiticsReportMapper.toModel(guardado);
    }

    // 4. EDITAR UN REPORTE EXISTENTE
    public AnaliticsReportModel editReport(Long id, AnaliticsReportRequestDto dto) {
        // 1. Busco el reporte original que queremos modificar
        Optional<AnaliticsReportEntity> resultadoReporte = analiticsReportRepository.findById(id);
        if (resultadoReporte.isEmpty()) {
            throw new NotFoundException("Reporte de analítica no encontrado con ID: " + id);
        }
        AnaliticsReportEntity entidad = resultadoReporte.get();

        // 2. Busco la organización para asegurarnos de que existe
        Optional<OrganizationEntity> resultadoOrg = organizationRepository.findById(dto.organizationId());
        if (resultadoOrg.isEmpty()) {
            throw new NotFoundException("Organización no encontrada con ID: " + dto.organizationId());
        }
        OrganizationEntity organizacion = resultadoOrg.get();

        // 3. Modifico los atributos uno a uno usando Setters
        entidad.setCo2SavingsKg(dto.co2SavingsKg());
        entidad.setEnergySavingsEuros(dto.energySavingsEuros());
        entidad.setTotalReservations(dto.totalReservations());
        entidad.setConfirmedCheckIns(dto.confirmedCheckIns());
        entidad.setEmptyRooms(dto.emptyRooms());
        entidad.setOrganization(organizacion); // Actualizo la organización

        // 4. Guardo los cambios sobre la misma entidad
        AnaliticsReportEntity modificado = analiticsReportRepository.save(entidad);
        
        return analiticsReportMapper.toModel(modificado);
    }

    // 5. ELIMINAR UN REPORTE
    public void deleteReport(Long id) {
        Optional<AnaliticsReportEntity> resultado = analiticsReportRepository.findById(id);
        
        // Compruebo si existe antes de intentar borrar
        if (resultado.isEmpty()) {
            throw new NotFoundException("No se puede eliminar. El reporte no existe con ID: " + id);
        }
        
        // Si existe, lo borro por su ID 
        analiticsReportRepository.deleteById(id);
    }
}