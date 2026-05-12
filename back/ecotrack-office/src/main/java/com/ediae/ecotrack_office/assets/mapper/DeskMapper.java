package com.ediae.ecotrack_office.assets.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.assets.dto.DeskResponseDto;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.model.DeskModel;

@Component
public class DeskMapper {

    public DeskModel fromEntity(DeskModel entity) {
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

    public DeskModel toEntity(DeskModel model) {
        if (model == null) {
            return null;
        }
        return new DeskModel(
            model.getId(),
            model.getName(),
            model.getStatus(),
            model.getIsActive(),
            model.getEquipmentList(),
            model.getRoomId()
        );
    }

    public DeskModel fromResponseDto(DeskResponseDto dto) {
        if (dto == null) {
            return null;
        }
        return new DeskModel(
            dto.getId(),
            dto.getName(),
            dto.getStatus() != null ? ResourceStatus.valueOf(dto.getStatus()) : null,
            dto.getIsActive(),
            dto.getEquipmentList(),
            dto.getRoomId()
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
