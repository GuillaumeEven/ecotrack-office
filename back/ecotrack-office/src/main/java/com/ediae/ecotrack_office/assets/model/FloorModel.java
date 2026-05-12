package com.ediae.ecotrack_office.assets.model;

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
}
