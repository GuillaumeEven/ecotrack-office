package com.ediae.ecotrack_office.assets.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.assets.dto.FloorCreateDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.model.FloorModel;

@Component
public class FloorMapper {

    public FloorModel fromEntity(FloorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new FloorModel(
            entity.getId(),
            entity.getLevel(),
            entity.getIsActive()
            // OrganizationModel.fromEntity(entity.getOrganization())
        );
    }

    public FloorEntity toNewEntity(FloorModel model) {
        FloorEntity entity = new FloorEntity();
        entity.setLevel(model.getLevel());
        entity.setIsActive(model.getIsActive());
        // if (model.getOrganization() != null) {
        //     entity.setOrganization(model.getOrganization().toEntity());
        // }
        return entity;
    }

    public FloorModel fromCreateRequestDto(FloorCreateDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        return new FloorModel(
            null,
            requestDto.getLevel(),
            requestDto.getIsActive()
            // OrganizationModel.fromRequestDto(requestDto.getOrganization())
        );
    }

    public FloorResponseDto toResponseDto(FloorModel model) {
        FloorResponseDto responseDto = new FloorResponseDto();
        responseDto.setId(model.getId());
        responseDto.setLevel(model.getLevel());
        responseDto.setIsActive(model.getIsActive());
        // if (model.getOrganization() != null) {
        //     responseDto.setOrganization(model.getOrganization().toResponseDto());
        // }
        return responseDto;
    }

}
