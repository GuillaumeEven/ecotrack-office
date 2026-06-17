package com.ediae.ecotrack_office.assets.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.mapper.FloorMapper;
import com.ediae.ecotrack_office.assets.model.FloorModel;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.repository.RoomRepository;
import com.ediae.ecotrack_office.assets.service.FloorService;
import com.ediae.ecotrack_office.shared.exception.ConstraintViolationException;


@Service
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorMapper floorMapper;

    @Override
    public List<FloorModel> getFloors() {
        List<FloorEntity> entities = floorRepository.findAll();
        return entities.stream()
            .map(floorMapper::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public FloorModel getFloorById(Long id) {
        FloorEntity entity = floorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));
        return floorMapper.fromEntity(entity);
    }

    @Override
    public List<FloorModel> getFloorsByOrganizationId(Long organizationId) {
        List<FloorEntity> entities = floorRepository.findByOrganizationId(organizationId);
        return entities.stream()
            .map(floorMapper::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public FloorModel createFloor(FloorRequestDto floorRequestDto) {
        FloorModel model = floorMapper.fromRequestDto(floorRequestDto);
        if (model.getName() == null) {
            model.setName("Floor " + model.getLevel());
        }
        FloorEntity entity = floorMapper.toEntity(model);
        FloorEntity savedEntity = floorRepository.save(entity);
        return floorMapper.fromEntity(savedEntity);
    }

    @Override
    public FloorModel updateFloor(Long id, FloorRequestDto floorRequestDto) {
        FloorEntity existingEntity = floorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));
        FloorModel model = floorMapper.fromRequestDto(floorRequestDto);
        if (model.getName() == null) {
            model.setName("Floor " + model.getLevel());
        }
        FloorEntity entity = floorMapper.toEntity(model);
        entity.setId(existingEntity.getId());
        FloorEntity updatedEntity = floorRepository.save(entity);
        return floorMapper.fromEntity(updatedEntity);
    }

    @Override
    public void deleteFloor(Long id) {
        if (!floorRepository.existsById(id)) {
            throw new RuntimeException("Floor not found with id: " + id);
        }

        // Check if floor has any rooms
        Long roomCount = roomRepository.countByFloor_Id(id);
        if (roomCount > 0) {
            throw new ConstraintViolationException("Cannot delete floor with id: " + id + ". It still has " + roomCount + " room(s). Please delete all rooms first.");
        }

        floorRepository.deleteById(id);
    }
}