package com.ediae.ecotrack_office.assets.service;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;


public interface FloorService {

    public FloorResponseDto getFloorById(Long id);

    public FloorResponseDto createFloor(FloorRequestDto floorRequestDto);

    public FloorResponseDto updateFloor(Long id, FloorRequestDto floorRequestDto);

    public void deleteFloor(Long id);

}
