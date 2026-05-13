package com.ediae.ecotrack_office.assets.dto;


public class FloorResponseDto {

    private Long id;
    private Integer level;
    private Boolean isActive;
    private Long organizationId;

    public FloorResponseDto() {
    }

    public FloorResponseDto(Long id, Integer level, Boolean isActive, Long organizationId) {
        this.id = id;
        this.level = level;
        this.isActive = isActive;
        this.organizationId = organizationId;
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
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
