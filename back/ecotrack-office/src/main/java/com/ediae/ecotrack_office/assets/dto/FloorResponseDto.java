package com.ediae.ecotrack_office.assets.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FloorResponseDto {

    private Long id;
    private Integer level;
    private Boolean isActive;
    private OrganizationDto organizationDto;

    public FloorResponseDto() {
    }

    public FloorResponseDto(Long id, Integer level, Boolean isActive, OrganizationDto organizationDto) {
        this.id = id;
        this.level = level;
        this.isActive = isActive;
        this.organizationDto = organizationDto;
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

    public Long getOrganizationId() {
        return organizationDto.getId();
    }

    public OrganizationDto getOrganizationDto() {
        return organizationDto;
    }

    public void setOrganizationDto(OrganizationDto organizationDto) {
        this.organizationDto = organizationDto;
    }
}
