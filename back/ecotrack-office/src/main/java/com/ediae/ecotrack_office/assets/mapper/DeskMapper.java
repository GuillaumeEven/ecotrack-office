package com.ediae.ecotrack_office.assets.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.assets.dto.DeskResponseDto;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.model.DeskModel;
import com.ediae.ecotrack_office.assets.entity.DeskEntity;

@Component
public class DeskMapper {

    public DeskModel fromEntity(DeskEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DeskModel(
            entity.getId(),
            entity.getName(),
            entity.getStatus(),
            entity.getIsActive(),
            entity.getEquipmentList(),
            entity.getRoomId()
        );
    }

    public DeskEntity toEntity(DeskModel model) {
        if (model == null) {
            return null;
        }
        DeskEntity entity = new DeskEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setIsActive(model.getIsActive());
        entity.setEquipmentList(model.getEquipmentList());
        entity.setRoomId(model.getRoomId());
        return entity;
    }

    public DeskResponseDto toResponseDto(DeskModel model) {
        if (model == null) {
            return null;
        }
        return new DeskResponseDto(
            model.getId(),
            model.getName(),
            model.getStatus() != null ? model.getStatus().name() : null,
            model.getIsActive(),
            model.getEquipmentList(),
            model.getRoomId()
        );
    }

    public DeskModel fromRequestDto(DeskRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return new DeskModel(
            null,
            dto.getName(),
            dto.getStatus() != null ? ResourceStatus.valueOf(dto.getStatus()) : null,
            dto.getIsActive(),
            dto.getEquipmentList(),
            dto.getRoomId()
        );
    }
}
