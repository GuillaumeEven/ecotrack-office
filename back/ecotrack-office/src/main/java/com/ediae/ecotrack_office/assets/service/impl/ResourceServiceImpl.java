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
import com.ediae.ecotrack_office.assets.repository.ResourceRepository;
import com.ediae.ecotrack_office.assets.service.ResourceService;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final double MAX_OCCUPANCY_RATIO = 0.8;

    @Autowired
    private ResourceRepository resourceRepository;

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
}
