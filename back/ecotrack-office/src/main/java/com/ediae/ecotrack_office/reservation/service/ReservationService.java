package com.ediae.ecotrack_office.reservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.ResourceStatusCalculatorService;
import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.mapper.ReservationMapper;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;

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

    public List <ReservationModel> getAllReservations () {

        List <ReservationEntity> entities = repository.findAll();
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

        ReservationModel model = ReservationMapper.fromCreateDto(dto);
        ReservationEntity entity = ReservationMapper.toEntity(model);
        entity.setStatus(ReservationStatus.CONFIRMED);
        ReservationEntity savedEntity = repository.save(entity);

        // Trigger desk_area unlock logic after creating reservation
        triggerDeskAreaUnlock(savedEntity);

        return ReservationMapper.fromEntity(savedEntity);
    }

    /**
     * Triggers desk_area unlock if the reserved desk's room reaches >= 80% occupancy on that date
     */
    private void triggerDeskAreaUnlock(ReservationEntity reservation) {
        try {
            // Verify if the reserved resource is a Desk
            if (!(reservation.getResource() instanceof DeskEntity)) {
                return; // Not a desk, no unlock needed
            }

            DeskEntity desk = (DeskEntity) reservation.getResource();
            RoomEntity room = desk.getRoom();

            if (room == null || !RoomType.DESK_AREA.equals(room.getType())) {
                return; // Not a desk_area, no unlock needed
            }

            // Calculate current room status for the reservation date
            ResourceStatus roomStatus = resourceStatusCalculatorService.calculateRoomStatus(room, reservation.getDate());

            // If room reached >= 80% occupancy, unlock the next available desk_area
            if (roomStatus == ResourceStatus.RESERVED) {
                RoomEntity nextDeskArea = resourceStatusCalculatorService.getNextAvailableDeskArea(room.getFloor(), reservation.getDate());

                if (nextDeskArea != null) {
                    nextDeskArea.setStatus(ResourceStatus.AVAILABLE);
                    roomRepository.save(nextDeskArea);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the reservation
            System.err.println("Error triggering desk_area unlock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ReservationModel updateReservationById (Long id, ReservationUpdateDto dto) {

        Optional <ReservationEntity> initialEntity = repository.findById(id);
        if(initialEntity.isEmpty()) {

            throw new RuntimeException("No se ha econtrado una reserva con id: " + id);
        }
        ReservationModel model = ReservationMapper.fromUpdateDto(dto);
        ReservationEntity savedEntity = repository.save(ReservationMapper.toEntity(model));
        return ReservationMapper.fromEntity(savedEntity);
    }

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
