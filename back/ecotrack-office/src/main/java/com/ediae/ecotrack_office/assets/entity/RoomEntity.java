package com.ediae.ecotrack_office.assets.entity;

import java.util.List;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;


@Entity
@Table(name = "ast_rooms")
public class RoomEntity extends ResourceEntity {

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(name = "surface_area", nullable = false)
    private Double surfaceArea;

    @ManyToOne(optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private FloorEntity floor;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, orphanRemoval = true)
private List<DeskEntity> desks;

    public RoomEntity() {
    }

    public RoomEntity(
        String name,
        ResourceStatus status,
        String equipmentList,
        RoomType type,
        Double surfaceArea,
        FloorEntity floor,
        Integer capacity
    ) {
        super(name, status, equipmentList);
        this.type = type;
        this.surfaceArea = surfaceArea;
        this.floor = floor;
        this.capacity = capacity;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
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
