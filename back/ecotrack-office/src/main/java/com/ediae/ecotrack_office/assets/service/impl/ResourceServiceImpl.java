package com.ediae.ecotrack_office.assets.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.repository.DeskRepository;
import com.ediae.ecotrack_office.assets.repository.ResourceRepository;
import com.ediae.ecotrack_office.assets.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final double MAX_OCCUPANCY_RATIO = 0.8;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Override
    public void updateStatus(Long resourceId, ResourceStatus newStatus) {
        ResourceEntity resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceId));

        // Desk-specific rule: block reservation if room is at or above 80% occupancy
        if (resource instanceof DeskEntity desk && newStatus == ResourceStatus.RESERVED) {
            checkRoomOccupancy(desk.getRoomId());
        }

        resource.setStatus(newStatus);
        resourceRepository.save(resource);
    }

    private void checkRoomOccupancy(Long roomId) {
        long total = deskRepository.countByRoomId(roomId);
        if (total == 0) return;

        long reserved = deskRepository.countByRoomIdAndStatus(roomId, ResourceStatus.RESERVED);

        if ((double) reserved / total >= MAX_OCCUPANCY_RATIO) {
            throw new IllegalStateException(
                "Room " + roomId + " has reached 80% occupancy — reservation denied"
            );
        }
    }
}
