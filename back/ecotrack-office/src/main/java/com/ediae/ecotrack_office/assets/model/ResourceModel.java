package com.ediae.ecotrack_office.assets.model;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public abstract class ResourceModel {

    private Long id;
    private String name;
    private ResourceStatus status;
    private Boolean isActive;
    private String equipmentList;

    protected ResourceModel() {
    }

    protected ResourceModel(
        Long id,
        String name,
        ResourceStatus status,
        Boolean isActive,
        String equipmentList
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isActive = isActive;
        this.equipmentList = equipmentList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(String equipmentList) {
        this.equipmentList = equipmentList;
    }

}
