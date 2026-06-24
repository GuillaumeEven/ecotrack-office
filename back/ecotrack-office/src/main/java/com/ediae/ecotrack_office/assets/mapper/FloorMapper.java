package com.ediae.ecotrack_office.assets.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;
import com.ediae.ecotrack_office.assets.model.FloorModel;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;

@Component
public class FloorMapper {

    public FloorModel fromEntity(FloorEntity entity) {
        if (entity == null) {
            return null;
        }
        Long organizationId = entity.getOrganization() != null ? entity.getOrganization().getId() : null;
        return new FloorModel(
            entity.getId(),
            entity.getLevel(),
            entity.getIsActive(),
            organizationId,
            entity.getName()
        );
    }

    public FloorEntity toEntity(FloorModel model) {
        if (model == null) {
            return null;
        }
        FloorEntity entity = new FloorEntity();
        entity.setId(model.getId());
        entity.setLevel(model.getLevel());
        entity.setIsActive(model.getIsActive());
        entity.setName(model.getName());
        if (model.getOrganizationId() != null) {
            OrganizationEntity orgRef = new OrganizationEntity();
            orgRef.setId(model.getOrganizationId());
            entity.setOrganization(orgRef);
        }
        return entity;
    }

    public FloorModel fromRequestDto(FloorRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        return new FloorModel(
            null,
            requestDto.getLevel(),
            requestDto.getIsActive(),
            requestDto.getOrganizationId(),
            requestDto.getName()
        );
    }

    public FloorResponseDto toResponseDto(FloorModel model) {
        if (model == null) {
            return null;
        }
        FloorResponseDto responseDto = new FloorResponseDto();
        responseDto.setId(model.getId());
        responseDto.setLevel(model.getLevel());
        responseDto.setIsActive(model.getIsActive());
        responseDto.setOrganizationId(model.getOrganizationId());
        responseDto.setName(model.getName());
        return responseDto;
    }

    public FloorResponseDto toResponseDto(FloorEntity entity) {
        if (entity == null) {
            return null;
        }
        Long organizationId = entity.getOrganization() != null ? entity.getOrganization().getId() : null;
        FloorResponseDto responseDto = new FloorResponseDto();
        responseDto.setId(entity.getId());
        responseDto.setLevel(entity.getLevel());
        responseDto.setIsActive(entity.getIsActive());
        responseDto.setOrganizationId(organizationId);
        responseDto.setName(entity.getName());
        return responseDto;
    }

}
