package com.ediae.ecotrack_office.assets.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.model.RoomModel;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomMapper roomMapper;


    @Override
    public RoomResponseDto getRoomById(Long roomId) {
        RoomEntity entity = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
        RoomModel roomModel = roomMapper.fromEntity(entity);
        return roomMapper.toResponseDto(roomModel);
    }

    @Override
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDTO) {

        RoomModel roomModel = roomMapper.fromRequestDto(roomRequestDTO);
        RoomEntity roomEntity = roomMapper.toEntity(roomModel);
        RoomEntity savedEntity = roomRepository.save(roomEntity);

        return roomMapper.toResponseDto(roomMapper.fromEntity(savedEntity));
    }


    @Override
    @Transactional
    public RoomResponseDto updateRoom(Long roomId, RoomRequestDto roomRequestDTO) {
        RoomEntity existingEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        RoomModel updatedModel = roomMapper.fromRequestDto(roomRequestDTO);

        // validate capacity against existing desks
        int currentDeskCount = deskRepository.findByRoomId(roomId).size();
        if (updatedModel.getCapacity() != null && updatedModel.getCapacity() < currentDeskCount) {
            throw new RuntimeException("Cannot set capacity lower than existing desks count (" + currentDeskCount + ")");
        }
        // floor would not be changeable
        if (updatedModel.getFloorId() != null && !updatedModel.getFloorId().equals(existingEntity.getFloorId())) {
            throw new RuntimeException("Cannot change floor of the room. Floor is immutable.");
        }

        // apply field updates
        roomMapper.updateEntityFromModel(updatedModel, existingEntity);


        RoomEntity updatedEntity = roomRepository.save(existingEntity);
        return roomMapper.toResponseDto(roomMapper.fromEntity(updatedEntity));
    }

    public void deleteRoomById(Long roomId) {
        RoomEntity existingEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        // Check if there are desks associated with the room
        if (!deskRepository.findByRoomId(roomId).isEmpty()) {
            throw new RuntimeException("Cannot delete room with associated desks. Please delete the desks first.");
        }

        roomRepository.delete(existingEntity);
    }
}
