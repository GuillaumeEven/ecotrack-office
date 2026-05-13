package com.ediae.ecotrack_office.assets.dto;


public abstract class ResourceRequestDto {

    private String name;
    private String status;
    private Boolean isActive;
    private String equipmentList;

    public ResourceRequestDto() {
    }

    public ResourceRequestDto(
        String name,
        String status,
        Boolean isActive,
        String equipmentList
    ) {
        this.name = name;
        this.status = status;
        this.isActive = isActive;
        this.equipmentList = equipmentList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
