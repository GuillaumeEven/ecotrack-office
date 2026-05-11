package com.ediae.ecotrack_office.assets.model;

import com.ediae.ecotrack_office.assets.dto.FloorCreateDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.dto.FloorUpdateDto;
import com.ediae.ecotrack_office.assets.entity.FloorEntity;

public class FloorModel {

    private Long id;
    private Integer level;
    private Boolean isActive;
    // private OrganizationModel organization;

    public FloorModel() {
    }

    public FloorModel(Long id, Integer level, Boolean isActive
        // OrganizationModel organization
    ) {
        this.id = id;
        this.level = level;
        this.isActive = isActive;
        // this.organization = organization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // public OrganizationModel getOrganization() {
    //     return organization;
    // }

    // public void setOrganization(OrganizationModel organization) {
    //     this.organization = organization;
    // }

    public static FloorModel fromEntity(FloorEntity entity) {
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

    public FloorEntity toEntity() {
        FloorEntity entity = new FloorEntity();
        entity.setId(this.id);
        entity.setLevel(this.level);
        entity.setIsActive(this.isActive);
        // if (this.organization != null) {
        //     entity.setOrganization(this.organization.toEntity());
        // }
        return entity;
    }

    public FloorEntity toNewEntity() {
        FloorEntity entity = new FloorEntity();
        entity.setLevel(this.level);
        entity.setIsActive(this.isActive);
        // if (this.organization != null) {
        //     entity.setOrganization(this.organization.toEntity());
        // }
        return entity;
    }

    public static FloorModel fromCreateRequestDto(FloorCreateDto requestDto) {
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

    public static FloorModel fromUpdateRequestDto(FloorUpdateDto requestDto, Long id) {
        if (requestDto == null) {
            return null;
        }
        return new FloorModel(
            id,
            requestDto.getLevel(),
            requestDto.getIsActive()
            // OrganizationModel.fromRequestDto(requestDto.getOrganization())
        );
    }

    public FloorResponseDto toResponseDto() {
        FloorResponseDto responseDto = new FloorResponseDto();
        responseDto.setId(this.id);
        responseDto.setLevel(this.level);
        responseDto.setIsActive(this.isActive);
        // if (this.organization != null) {
        //     responseDto.setOrganization(this.organization.toResponseDto());
        // }
        return responseDto;
    }
}
