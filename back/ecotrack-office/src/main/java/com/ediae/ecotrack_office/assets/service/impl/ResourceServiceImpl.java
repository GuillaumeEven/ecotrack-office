package com.ediae.ecotrack_office.assets.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.ResourceRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final double MAX_OCCUPANCY_RATIO = 0.8;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void updateStatus(Long resourceId, ResourceStatus newStatus) {
        ResourceEntity resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceId));

        resource.setStatus(newStatus);
        resourceRepository.save(resource);

        // After saving, check if the room occupancy threshold is crossed
        if (resource instanceof DeskEntity desk) {
            syncRoomStatus(desk.getRoomId());
        }
    }

    // When a desk is reserved and its room crosses 80% occupancy:
    // - the room becomes RESERVED (remaining desks still bookable)
    // - the next UNAVAILABLE room on the same floor opens up (AVAILABLE)
    private void syncRoomStatus(Long roomId) {
        long total = deskRepository.countByRoomId(roomId);
        if (total == 0) return;

        long reserved = deskRepository.countByRoomIdAndStatus(roomId, ResourceStatus.RESERVED);
        if ((double) reserved / total < MAX_OCCUPANCY_RATIO) return;

        RoomEntity room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        // Guard: only trigger once (avoid re-opening a new room on every subsequent booking)
        if (room.getStatus() != ResourceStatus.AVAILABLE) return;

        room.setStatus(ResourceStatus.RESERVED);
        roomRepository.save(room);

        Long floorId = room.getFloor().getId();
        RoomEntity nextRoom = roomRepository
            .findFirstByFloorIdAndStatusOrderByIdAsc(floorId, ResourceStatus.UNAVAILABLE)
            .orElse(null);

        if (nextRoom != null) {
            nextRoom.setStatus(ResourceStatus.AVAILABLE);
            roomRepository.save(nextRoom);
        }
    }
}
