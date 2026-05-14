package com.ediae.ecotrack_office.assets.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.mapper.FloorMapper;
import com.ediae.ecotrack_office.assets.model.FloorModel;
import com.ediae.ecotrack_office.assets.repository.FloorRepository;
import com.ediae.ecotrack_office.assets.service.FloorService;


@Service
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private FloorMapper floorMapper;

    @Override
    public FloorResponseDto getFloorById(Long id) {
        FloorEntity entity = floorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));
        return floorMapper.toResponseDto(floorMapper.fromEntity(entity));
    }

    @Override
    public FloorResponseDto createFloor(FloorRequestDto floorRequestDto) {
        FloorModel model = floorMapper.fromRequestDto(floorRequestDto);
        FloorEntity entity = floorMapper.toEntity(model);
        FloorEntity savedEntity = floorRepository.save(entity);
        return floorMapper.toResponseDto(floorMapper.fromEntity(savedEntity));
    }

    @Override
    public FloorResponseDto updateFloor(Long id, FloorRequestDto floorRequestDto) {
        FloorEntity existingEntity = floorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));
        FloorModel model = floorMapper.fromRequestDto(floorRequestDto);
        FloorEntity entity = floorMapper.toEntity(model);
        entity.setId(existingEntity.getId());
        FloorEntity updatedEntity = floorRepository.save(entity);
        return floorMapper.toResponseDto(floorMapper.fromEntity(updatedEntity));
    }

    @Override
    public void deleteFloor(Long id) {
        if (!floorRepository.existsById(id)) {
            throw new RuntimeException("Floor not found with id: " + id);
        }
        floorRepository.deleteById(id);
    }
}