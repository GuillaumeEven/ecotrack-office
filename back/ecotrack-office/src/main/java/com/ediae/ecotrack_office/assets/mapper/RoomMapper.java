package com.ediae.ecotrack_office.assets.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;
import com.ediae.ecotrack_office.assets.model.RoomModel;

@Component
public class RoomMapper {

    public RoomModel fromEntity(RoomEntity entity) {
        if (entity == null) {
            return null;
        }

        List<Long> deskIds = entity.getDesks() != null
            ? entity.getDesks().stream().map(d -> d.getId()).toList()
            : List.of();

        return new RoomModel(
            entity.getId(),
            entity.getName(),
            entity.getStatus(),
            entity.getIsActive(),
            entity.getEquipmentList(),
            entity.getType(),
            entity.getSurfaceArea(),
            entity.getFloorId(),
            entity.getCapacity(),
            deskIds
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

        if (model.getFloorId() != null) {
            FloorEntity floorRef = new FloorEntity();
            floorRef.setId(model.getFloorId());
            entity.setFloor(floorRef);
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
            model.getFloorId(),
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
        model.setFloorId(dto.getFloorId());

        return model;
    }

    public void updateEntityFromModel(RoomModel model, RoomEntity entity) {
        if (model == null || entity == null) {
            return;
        }
        if (model.getName() != null) {
            entity.setName(model.getName());
        }
        if (model.getStatus() != null) {
            entity.setStatus(model.getStatus());
        }
        if (model.getIsActive() != null) {
            entity.setIsActive(model.getIsActive());
        }
        if (model.getEquipmentList() != null) {
            entity.setEquipmentList(model.getEquipmentList());
        }
        if (model.getType() != null) {
            entity.setType(model.getType());
        }
        if (model.getSurfaceArea() != null) {
            entity.setSurfaceArea(model.getSurfaceArea());
        }
        if (model.getCapacity() != null) {
            entity.setCapacity(model.getCapacity());
        }
        if (model.getFloorId() != null) {
            FloorEntity floorRef = new FloorEntity();
            floorRef.setId(model.getFloorId());
            entity.setFloor(floorRef);
        }
    }

}
