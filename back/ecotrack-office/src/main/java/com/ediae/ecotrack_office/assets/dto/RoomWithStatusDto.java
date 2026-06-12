package com.ediae.ecotrack_office.assets.dto;

import java.util.List;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public class RoomWithStatusDto {

    private RoomResponseDto room;
    private List<DeskWithStatusDto> desks;
    private Double occupancyRate;
    private ResourceStatus roomStatus;

    public RoomWithStatusDto() {
    }

    public RoomWithStatusDto(RoomResponseDto room, List<DeskWithStatusDto> desks, Double occupancyRate, ResourceStatus roomStatus) {
        this.room = room;
        this.desks = desks;
        this.occupancyRate = occupancyRate;
        this.roomStatus = roomStatus;
    }

    public RoomResponseDto getRoom() {
        return room;
    }

    public void setRoom(RoomResponseDto room) {
        this.room = room;
    }

    public List<DeskWithStatusDto> getDesks() {
        return desks;
    }

    public void setDesks(List<DeskWithStatusDto> desks) {
        this.desks = desks;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public ResourceStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(ResourceStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

}
