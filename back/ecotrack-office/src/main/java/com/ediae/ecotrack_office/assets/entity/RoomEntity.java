package com.ediae.ecotrack_office.assets.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ast_rooms")
public class RoomEntity extends ResourceEntity {

    @Column(name = "surface_area", nullable = false)
    private Double surfaceArea;

    @ManyToOne(optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private FloorEntity floor;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "room")
    private List<DeskEntity> desks;

    public RoomEntity() {
    }

    public RoomEntity(
        String name,
        ResourceStatus status,
        FloorEntity floor,
        Double surfaceArea,
        Integer capacity,
        String equipmentList
    ) {
        super(name, status, equipmentList);
        this.floor = floor;
        this.surfaceArea = surfaceArea;
        this.capacity = capacity;
        this.setEquipmentList(equipmentList);
    }

    public Double getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(Double surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public FloorEntity getFloor() {
        return floor;
    }

    public Long getFloorId() {
        return floor != null ? floor.getId() : null;
    }

    public void setFloor(FloorEntity floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<DeskEntity> getDesks() {
        return desks;
    }

    public void setDesks(List<DeskEntity> desks) {
        this.desks = desks;
    }
}
