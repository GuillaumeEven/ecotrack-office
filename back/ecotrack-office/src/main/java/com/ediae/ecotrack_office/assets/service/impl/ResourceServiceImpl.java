package com.ediae.ecotrack_office.assets.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.mapper.DeskMapper;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.model.ResourceModel;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.ResourceRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.ResourceService;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final double MAX_OCCUPANCY_RATIO = 0.8;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private DeskMapper deskMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public void updateStatus(Long resourceId, ResourceStatus newStatus) {
        ResourceEntity resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceId));

        resource.setStatus(newStatus);
        resourceRepository.save(resource);

        // TODO: DEPRECATED - Old sync logic replaced by ResourceStatusCalculatorService
        // Status calculation is now dynamic and date-based instead of static
        // After saving, check if the room occupancy threshold is crossed
        // if (resource instanceof DeskEntity desk) {
        //     syncRoomStatus(desk.getRoomId());
        // }
    }

    @Override
    public ResourceModel getResourceById(Long resourceId) {
        ResourceEntity resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

        if (resource instanceof DeskEntity desk) {
            return deskMapper.fromEntity(desk);
        } else if (resource instanceof RoomEntity room) {
            return roomMapper.fromEntity(room);
        } else {
            throw new NotFoundException("Resource type not supported for id: " + resourceId);
        }
    }

    // TODO: DEPRECATED METHOD - Use ResourceStatusCalculatorService.getFloorWithStatusForDate() instead
    // This method implements the old static status update logic that is now replaced by dynamic calculation
    // Kept for reference; can be deleted after Phase 2 database migration
    /*
    private void syncRoomStatus(Long roomId) {
        long total = deskRepository.countByRoom_Id(roomId);
        if (total == 0) return;

        long reserved = deskRepository.countByRoom_IdAndStatus(roomId, ResourceStatus.RESERVED);
        if ((double) reserved / total < MAX_OCCUPANCY_RATIO) return;

        RoomEntity room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        // Guard: only trigger once (avoid re-opening a new room on every subsequent booking)
        if (room.getStatus() != ResourceStatus.AVAILABLE) return;

        room.setStatus(ResourceStatus.RESERVED);
        roomRepository.save(room);

        Long floorId = room.getFloor().getId();
        RoomEntity nextRoom = roomRepository
            .findFirstByFloor_IdAndStatusOrderByIdAsc(floorId, ResourceStatus.UNAVAILABLE)
            .orElse(null);

        if (nextRoom != null) {
            nextRoom.setStatus(ResourceStatus.AVAILABLE);
            roomRepository.save(nextRoom);
        }
    }
    */
}
