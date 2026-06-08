package com.ediae.ecotrack_office.assets.service;

import java.util.List;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.model.FloorModel;


public interface FloorService {

    public FloorModel getFloorById(Long id);

    public List<FloorModel> getFloorsByOrganizationId(Long organizationId);

    public FloorModel createFloor(FloorRequestDto floorRequestDto);

    public FloorModel updateFloor(Long id, FloorRequestDto floorRequestDto);

    public void deleteFloor(Long id);

}
