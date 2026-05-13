package com.ediae.ecotrack_office.assets.model;

import java.util.List;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;

public class RoomModel extends ResourceModel {

    private RoomType type;
    private Double surfaceArea;
    private Long floorId;
    private Integer capacity;
    private List<Long> deskIds;

    public RoomModel() {
    }

    public RoomModel(
        Long id,
        String name,
        ResourceStatus status,
        Boolean isActive,
        String equipmentList,
        RoomType type,
        Double surfaceArea,
        Long floorId,
        Integer capacity,
        List<Long> deskIds
    ) {
        super(id, name, status, isActive, equipmentList);
        this.surfaceArea = surfaceArea;
        this.capacity = capacity;
        this.floorId = floorId;
        this.type = type;
        this.deskIds = deskIds;
    }

    public Double getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(Double surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public List<Long> getDeskIds() {
        return deskIds;
    }

    public void setDeskIds(List<Long> deskIds) {
        this.deskIds = deskIds;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

}
