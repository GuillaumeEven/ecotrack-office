package com.ediae.ecotrack_office.assets.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.model.RoomModel;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.RoomService;
import com.ediae.ecotrack_office.incident.mapper.IncidentMapper;
import com.ediae.ecotrack_office.incident.model.IncidentModel;
import com.ediae.ecotrack_office.shared.exception.BusinessRuleViolationException;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;

import jakarta.validation.ConstraintDeclarationException;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<RoomModel> getRooms() {
        List<RoomEntity> entities = roomRepository.findAll();
        return entities.stream()
                .map(roomMapper::fromEntity)
                .toList();
    }

    @Override
    public RoomModel getRoomById(Long roomId) {
        RoomEntity entity = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));
        RoomModel roomModel = roomMapper.fromEntity(entity);
        return roomModel;
    }

    @Override
    public List<RoomModel> getRoomsByFloorId(Long floorId) {
        List<RoomEntity> entities = roomRepository.findByFloor_Id(floorId);
        return entities.stream()
                .map(roomMapper::fromEntity)
                .toList();
    }

    @Override
    public List<IncidentModel> getIncidentsByRoomId(Long roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));
        return roomEntity.getIncidents().stream()
                .map(incidentEntity -> IncidentMapper.toModel(incidentEntity))
                .toList();
    }

    @Override
    public RoomModel createRoom(RoomRequestDto roomRequestDTO) {

        RoomModel roomModel = roomMapper.fromRequestDto(roomRequestDTO);
        RoomEntity roomEntity = roomMapper.toEntity(roomModel);
        RoomEntity savedEntity = roomRepository.save(roomEntity);

        return roomMapper.fromEntity(savedEntity);
    }


    @Override
    @Transactional
    public RoomModel updateRoom(Long roomId, RoomRequestDto roomRequestDTO) {
        RoomEntity existingEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        RoomModel updatedModel = roomMapper.fromRequestDto(roomRequestDTO);

        // validate capacity against existing desks
        int currentDeskCount = deskRepository.findByRoom_Id(roomId).size();
        if (updatedModel.getCapacity() != null && updatedModel.getCapacity() < currentDeskCount) {
            throw new BusinessRuleViolationException("Cannot reduce room capacity below current desk count. Current desk count: " + currentDeskCount);
        }
        // floor would not be changeable
        if (updatedModel.getFloorId() != null && !updatedModel.getFloorId().equals(existingEntity.getFloorId())) {
            throw new BusinessRuleViolationException(
                    "Cannot change floor of the room. Floor is immutable.");
        }

        // apply field updates
        roomMapper.updateEntityFromModel(updatedModel, existingEntity);

        RoomEntity updatedEntity = roomRepository.save(existingEntity);
        return roomMapper.fromEntity(updatedEntity);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        RoomEntity existingEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        // Check if there are desks associated with the room
        if (!deskRepository.findByRoom_Id(roomId).isEmpty()) {
            throw new ConstraintDeclarationException("Cannot delete room with associated desks. Please delete the desks first.");
        }

        roomRepository.delete(existingEntity);
    }

}
