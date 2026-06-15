package com.ediae.ecotrack_office.assets.dto;

import java.util.List;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public class RoomWithStatusDto {

    private RoomResponseDto room;
    private List<DeskWithStatusDto> desks;
    private Double occupancyRate;
    private ResourceStatus roomStatus;
    private String reservedBy; // For meeting rooms: email of user who reserved
    private Long reservationId; // For meeting rooms: ID of the reservation

    public RoomWithStatusDto() {
    }

    public RoomWithStatusDto(RoomResponseDto room, List<DeskWithStatusDto> desks, Double occupancyRate, ResourceStatus roomStatus) {
        this.room = room;
        this.desks = desks;
        this.occupancyRate = occupancyRate;
        this.roomStatus = roomStatus;
    }

    public RoomWithStatusDto(RoomResponseDto room, List<DeskWithStatusDto> desks, Double occupancyRate, ResourceStatus roomStatus, String reservedBy, Long reservationId) {
        this.room = room;
        this.desks = desks;
        this.occupancyRate = occupancyRate;
        this.roomStatus = roomStatus;
        this.reservedBy = reservedBy;
        this.reservationId = reservationId;
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

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

}
