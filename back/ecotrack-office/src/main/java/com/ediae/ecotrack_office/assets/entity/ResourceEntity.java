package com.ediae.ecotrack_office.assets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ast_resources")
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "status", nullable = false)
    private ResourceStatus status;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "floor_id", nullable = false)
    private Long floorId;

    @Column(name = "equipment_list", nullable = true)
    private String equipmentList;

    public ResourceEntity() {
    }

    public ResourceEntity(
        String name,
        ResourceStatus status,
        Long floorId,
        String equipmentList
    ) {
        this.name = name;
        this.status = status;
        this.isActive = false;
        this.floorId = floorId;
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

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public String getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(String equipmentList) {
        this.equipmentList = equipmentList;
    }

}
