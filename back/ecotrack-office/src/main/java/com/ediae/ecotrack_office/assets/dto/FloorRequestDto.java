package com.ediae.ecotrack_office.assets.dto;

public class FloorRequestDto {

    private Integer level;
    private Boolean isActive;
    private Long organizationId;
    private String name;

    public FloorRequestDto() {
    }

    public FloorRequestDto(Integer level, Boolean isActive, Long organizationId) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
