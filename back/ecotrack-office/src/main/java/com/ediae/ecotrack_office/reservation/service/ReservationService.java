package com.ediae.ecotrack_office.reservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.ResourceRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.ResourceStatusCalculatorService;
import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.mapper.ReservationMapper;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;
import com.ediae.ecotrack_office.shared.exception.ForbiddenException;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.repository.UserRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private ResourceStatusCalculatorService resourceStatusCalculatorService;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    public List <ReservationModel> getReservationsByUserId (Long userId) {

        List <ReservationEntity> entities = repository.findByUserId(userId);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public List <ReservationModel> getReservationsByResourceId (Long resourceId) {

        List <ReservationEntity> entities = repository.findByResourceId(resourceId);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public List <ReservationModel> getReservationsByFloorIdAndDate (Long floorId, String date) {

        LocalDate localDate = LocalDate.parse(date);
        List <ReservationEntity> entities = repository.findByFloorIdAndDate(floorId, localDate);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public List <ReservationModel> getAllReservationsByOrganizationId (Long organizationId) {

        List <ReservationEntity> entities = repository.findByOrganizationId(organizationId);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public ReservationModel getReservationById (Long id) {

        Optional <ReservationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una reserva con id: " + id);
        }
        return ReservationMapper.fromEntity(entity.get());
    }

    public ReservationModel createReservation (ReservationCreateDto dto) {

        // Load entities from IDs
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + dto.getUserId()));

        var resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new NotFoundException("Recurso no encontrado con id: " + dto.getResourceId()));

        // Create entity directly
        ReservationEntity entity = new ReservationEntity(dto.getDate(), dto.getStatus(), user, resource);
        ReservationEntity savedEntity = repository.save(entity);

        return ReservationMapper.fromEntity(savedEntity);
    }

    
    public ReservationModel updateReservationById (Long id, ReservationUpdateDto dto) {

        Optional <ReservationEntity> initialEntity = repository.findById(id);
        if(initialEntity.isEmpty()) {

            throw new RuntimeException("No se ha econtrado una reserva con id: " + id);
        }
        ReservationEntity entity = initialEntity.get();
        entity.setDate(dto.getDate());
        ReservationEntity savedEntity = repository.save(entity);
        return ReservationMapper.fromEntity(savedEntity);
    }

    public Boolean deleteReservationById (Long id, Long currentUserId) {

        Optional <ReservationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("No se ha encontrado una reserva con id: " + id);
        }
        ReservationEntity reservation = entity.get();

        // Authorization check should be in controller using RoleGuard
        if (!reservation.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("No tienes permisos para eliminar esta reserva.");
        }

        repository.deleteById(id);

        return repository.findById(id).isEmpty();
    }

    // Keep old method for backward compatibility (no authorization check)
    public Boolean deleteReservationById (Long id) {

        Optional <ReservationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una reserva con id: " + id);
        }
        repository.deleteById(id);
        if (repository.findById(id).isEmpty()) return true;
        else return false;
    }
}
