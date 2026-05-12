package com.ediae.ecotrack_office.assets.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.model.DeskModel;
import com.ediae.ecotrack_office.assets.model.FloorModel;
import com.ediae.ecotrack_office.assets.model.RoomModel;

@Component
public class RoomMapper {

    public RoomModel fromEntity(RoomEntity entity) {

        if (entity == null) {
            return null;
        }

        List<DeskEntity> deskEntities =  entity.getDesks();

        FloorEntity floorEntity = entity.getFloor() != null ? entity.getFloor() : null;

        return new RoomModel(
            entity.getId(),
            entity.getName(),
            entity.getStatus(),
            entity.getIsActive(),
            entity.getEquipmentList(),
            entity.getType(),
            entity.getSurfaceArea(),
            floorEntity != null ? new FloorModel(
                floorEntity.getId(),
                floorEntity.getLevel(),
                floorEntity.getIsActive()
            ) : null,
            entity.getCapacity(),
            deskEntities != null ? deskEntities.stream().map(deskEntity -> {
                DeskModel deskModel = new DeskModel();
                deskModel.setId(deskEntity.getId());
                deskModel.setName(deskEntity.getName());
                deskModel.setStatus(deskEntity.getStatus());
                deskModel.setIsActive(deskEntity.getIsActive());
                deskModel.setEquipmentList(deskEntity.getEquipmentList());
                return deskModel;
            }).toList() : null
        );
    }

    public RoomEntity toEntity(RoomModel model) {

        if (model == null) {
            return null;
        }

        RoomEntity entity = new RoomEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setIsActive(model.getIsActive());
        entity.setEquipmentList(model.getEquipmentList());
        entity.setType(model.getType());
        entity.setSurfaceArea(model.getSurfaceArea());
        entity.setCapacity(model.getCapacity());

        if (model.getFloor() != null) {
            FloorEntity floorEntity = new FloorEntity(
                model.getFloor().getLevel(),
                model.getFloor().getIsActive(),
                null
                // model.getFloor().getOrganization() != null ? model.getFloor().getOrganization().toEntity() : null
            );
            entity.setFloor(floorEntity);
        }

        return entity;
    }

    public RoomResponseDto toResponseDto(RoomModel model) {
        if (model == null) {
            return null;
        }

        return new RoomResponseDto(
            model.getId(),
            model.getName(),
            model.getStatus() != null ? model.getStatus().name() : null,
            model.getIsActive(),
            model.getEquipmentList(),
            model.getType() != null ? model.getType().name() : null,
            model.getSurfaceArea(),
            model.getFloor() != null ? model.getFloor().getId() : null,
            model.getCapacity()
        );
    }

    public RoomModel fromRequestDto(RoomRequestDto dto) {
        if (dto == null) {
            return null;
        }

        RoomModel model = new RoomModel();
        model.setName(dto.getName());
        model.setStatus(dto.getStatus() != null ? ResourceStatus.valueOf(dto.getStatus()) : null);
        model.setIsActive(dto.getIsActive());
        model.setEquipmentList(dto.getEquipmentList());
        model.setType(dto.getRoomType() != null ? RoomType.valueOf(dto.getRoomType()) : null);
        model.setSurfaceArea(dto.getSurfaceArea());
        model.setCapacity(dto.getCapacity());

        if (dto.getFloorId() != null) {
            FloorModel floorModel = new FloorModel();
            floorModel.setId(dto.getFloorId());
            model.setFloor(floorModel);
        }

        return model;
    }

}
