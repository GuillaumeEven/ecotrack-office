package com.ediae.ecotrack_office.analyticsreport.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.analyticsreport.dto.AnalyticsReportGenerateDto;
import com.ediae.ecotrack_office.analyticsreport.dto.AnalyticsReportRequestDto;
import com.ediae.ecotrack_office.analyticsreport.entity.AnalyticsReportEntity;
import com.ediae.ecotrack_office.analyticsreport.mapper.AnalyticsReportMapper;
import com.ediae.ecotrack_office.analyticsreport.model.AnalyticsReportModel;
import com.ediae.ecotrack_office.analyticsreport.repository.AnalyticsReportRepository;
import com.ediae.ecotrack_office.assets.dto.DeskWithStatusDto;
import com.ediae.ecotrack_office.assets.dto.FloorWithStatusDto;
import com.ediae.ecotrack_office.assets.dto.RoomWithStatusDto;
import com.ediae.ecotrack_office.assets.repository.ResourceRepository;
import com.ediae.ecotrack_office.assets.service.ResourceStatusCalculatorService;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.repository.UserRepository;

@Service
public class AnalyticsService {

    private final AnalyticsReportRepository analyticsReportRepository;
    private final OrganizationRepository organizationRepository;
    private final AnalyticsReportMapper analyticsReportMapper;
    // private final UserRepository userRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ResourceRepository resourceRepository;

    @Autowired
    private ResourceStatusCalculatorService resourceStatusCalculatorService;

    // Constructor estándar para inyectar las dependencias
    public AnalyticsService(AnalyticsReportRepository analyticsReportRepository,
                            OrganizationRepository organizationRepository,
                            AnalyticsReportMapper analyticsReportMapper) {
        this.analyticsReportRepository = analyticsReportRepository;
        this.organizationRepository = organizationRepository;
        this.analyticsReportMapper = analyticsReportMapper;
    }

    // 1. OBTENER TODOS LOS REPORTES (Bucle tradicional)
    public List<AnalyticsReportModel> getAllReports(Long userId) {

        // Obtengo la organización del usuario
        Long organizationId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId))
                .getOrganization()
                .getId();

        List<AnalyticsReportEntity> listaEntidades = analyticsReportRepository.findByOrganizationId(organizationId);
        List<AnalyticsReportModel> listaModelos = new ArrayList<>();

        // Recorro la lista de la base de datos uno a uno y los convierto a modelos usando el Mapper
        for (AnalyticsReportEntity entidad : listaEntidades) {
            AnalyticsReportModel modelo = analyticsReportMapper.toModel(entidad);
            listaModelos.add(modelo);
        }

        return listaModelos;
    }

    // 2. OBTENER UN REPORTE POR SU ID
    public AnalyticsReportModel getReportById(Long id) {
        Optional<AnalyticsReportEntity> resultado = analyticsReportRepository.findById(id);

        // Si no existe en la base de datos, lanzo una excepción personalizada de "No encontrado"
        if (resultado.isEmpty()) {
            throw new NotFoundException("Reporte de analítica no encontrado con ID: " + id);
        }

        // Si existe, lo saco de la Optional, lo convierto a modelo y lo devuelvo
        AnalyticsReportEntity entidad = resultado.get();
        return analyticsReportMapper.toModel(entidad);
    }


    // 3. CREAR UN NUEVO REPORTE
    public AnalyticsReportModel createReport(AnalyticsReportRequestDto dto) {
        // Busco si existe la organización que manda en el DTO
        Optional<OrganizationEntity> resultadoOrg = organizationRepository.findById(dto.organizationId());
        if (resultadoOrg.isEmpty()) {
            throw new NotFoundException("Organización no encontrada con ID: " + dto.organizationId());
        }
        OrganizationEntity organizacion = resultadoOrg.get();

        // Convierto el DTO a Entidad limpia
        AnalyticsReportEntity entidad = analyticsReportMapper.toEntity(dto);

        // Le asigno la organización a la entidad antes de guardarla, porque el Mapper no tiene esa información
        entidad.setOrganization(organizacion);

        // Guardo en la base de datos
        AnalyticsReportEntity guardado = analyticsReportRepository.save(entidad);

        // Devuelvo el resultado pasado a modelo
        return analyticsReportMapper.toModel(guardado);
    }


    public AnalyticsReportModel generateReport(Long userId, AnalyticsReportGenerateDto dto) {

        Long organizationId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId))
                .getOrganization()
                .getId();

       List<FloorWithStatusDto> floorData = resourceStatusCalculatorService.calculateFloorsStatusForDate(organizationId, dto.dateReport());

       Double co2SavingsKg = 0.0;
       Double energySavingsEuros = 0.0;
       Integer totalReservations = 0;
       Integer confirmedCheckIns = 0;
       Integer emptyRooms = 0;

        for (FloorWithStatusDto floor : floorData) {
            for (RoomWithStatusDto room : floor.getRooms()) {
                if (room.getRoom().getRoomType().equals("DESK_AREA")) {
                    List<DeskWithStatusDto> desks = room.getDesks();
                    for (DeskWithStatusDto desk : desks) {
                        if (desk.getReservedBy() == null) {
                            co2SavingsKg += 0.5;
                            energySavingsEuros += 0.1;
                        } else {
                            totalReservations++;
                        }
                    }
                } else if (room.getRoom().getRoomType().equals("MEETING_ROOM")) {
                    if (room.getReservedBy() == null) {
                        co2SavingsKg += 1.0;
                        energySavingsEuros += 0.2;
                    } else {
                        totalReservations++;
                    }
                }
                if (room.getOccupancyRate() == 0.0) {
                    emptyRooms++;
                }
            }
        }

        confirmedCheckIns = totalReservations;

        OrganizationEntity organizacion = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organización no encontrada con ID: " + organizationId));

        // Convierto el DTO a Entidad limpia
        AnalyticsReportEntity entidad = new AnalyticsReportEntity();
        entidad.setCo2SavingsKg(co2SavingsKg);
        entidad.setEnergySavingsEuros(energySavingsEuros);
        entidad.setTotalReservations(totalReservations);
        entidad.setConfirmedCheckIns(confirmedCheckIns);
        entidad.setEmptyRooms(emptyRooms);
        entidad.setOrganization(organizacion);
        entidad.setGeneratedAt(LocalDateTime.now());

        // Guardo en la base de datos
        AnalyticsReportEntity guardado = analyticsReportRepository.save(entidad);

        // Devuelvo el resultado pasado a modelo
        return analyticsReportMapper.toModel(guardado);
    }



    // 4. EDITAR UN REPORTE EXISTENTE
    public AnalyticsReportModel editReport(Long id, AnalyticsReportRequestDto dto) {
        // 1. Busco el reporte original que queremos modificar
        Optional<AnalyticsReportEntity> resultadoReporte = analyticsReportRepository.findById(id);
        if (resultadoReporte.isEmpty()) {
            throw new NotFoundException("Reporte de analítica no encontrado con ID: " + id);
        }
        AnalyticsReportEntity entidad = resultadoReporte.get();

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
        AnalyticsReportEntity modificado = analyticsReportRepository.save(entidad);

        return analyticsReportMapper.toModel(modificado);
    }

    // 5. ELIMINAR UN REPORTE
    public void deleteReport(Long id) {
        Optional<AnalyticsReportEntity> resultado = analyticsReportRepository.findById(id);

        // Compruebo si existe antes de intentar borrar
        if (resultado.isEmpty()) {
            throw new NotFoundException("No se puede eliminar. El reporte no existe con ID: " + id);
        }

        // Si existe, lo borro por su ID
        analyticsReportRepository.deleteById(id);
    }
}