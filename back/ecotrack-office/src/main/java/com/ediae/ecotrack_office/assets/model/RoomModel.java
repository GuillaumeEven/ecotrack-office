package com.ediae.ecotrack_office.assets.model;

import java.util.List;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;

public class RoomModel extends ResourceModel {

    private RoomType type;
    private Double surfaceArea;
    private FloorModel floor;
    private Integer capacity;
    private List<DeskModel> desks;

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
        FloorModel floor,
        Integer capacity,
        List<DeskModel> desks
    ) {
        super(id, name, status, isActive, equipmentList);
        this.surfaceArea = surfaceArea;
        this.capacity = capacity;
        this.floor = floor;
        this.type = type;
        this.desks = desks;
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

    public FloorModel getFloor() {
        return floor;
    }

    public void setFloor(FloorModel floor) {
        this.floor = floor;
    }

    public List<DeskModel> getDesks() {
        return desks;
    }

    public void setDesks(List<DeskModel> desks) {
        this.desks = desks;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

}
