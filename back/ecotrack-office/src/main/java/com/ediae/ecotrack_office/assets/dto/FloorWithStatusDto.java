package com.ediae.ecotrack_office.assets.dto;

import java.time.LocalDate;
import java.util.List;

public class FloorWithStatusDto {

    private FloorResponseDto floor;
    private List<RoomWithStatusDto> rooms;
    private LocalDate date;

    public FloorWithStatusDto() {
    }

    public FloorWithStatusDto(FloorResponseDto floor, List<RoomWithStatusDto> rooms, LocalDate date) {
        this.floor = floor;
        this.rooms = rooms;
        this.date = date;
    }

    public FloorResponseDto getFloor() {
        return floor;
    }

    public void setFloor(FloorResponseDto floor) {
        this.floor = floor;
    }

    public List<RoomWithStatusDto> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomWithStatusDto> rooms) {
        this.rooms = rooms;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
