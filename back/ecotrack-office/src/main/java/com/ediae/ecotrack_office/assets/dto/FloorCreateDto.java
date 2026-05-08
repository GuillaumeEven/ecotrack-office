package com.ediae.ecotrack_office.assets.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FloorCreateDto {

    private Integer level;
    private Boolean isActive;
    private Long organizationId;

    public FloorCreateDto() {
    }

    public FloorCreateDto(Integer level, Boolean isActive, Long organizationId) {
        this.level = level;
        this.isActive = isActive;
        this.organizationId = organizationId;
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
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

}
