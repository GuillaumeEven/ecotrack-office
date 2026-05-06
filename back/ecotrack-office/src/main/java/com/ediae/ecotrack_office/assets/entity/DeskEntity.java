package com.ediae.ecotrack_office.assets.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ast_desks")
public class DeskEntity extends ResourceEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    public DeskEntity() {
    }

    public DeskEntity(
        String name,
        ResourceStatus status,
        String equipmentList,
        RoomEntity room
    ) {
        super(name, status, equipmentList);
        this.room = room;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public Long getRoomId() {
        return room != null ? room.getId() : null;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }
}
