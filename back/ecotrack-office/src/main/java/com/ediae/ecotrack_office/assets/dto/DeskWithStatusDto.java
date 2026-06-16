package com.ediae.ecotrack_office.assets.dto;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public class DeskWithStatusDto {

    private DeskResponseDto desk;
    private ResourceStatus calculatedStatus;
    private String reservedBy;
    private Long reservationId; // ID of the reservation (for cancellation)

    public DeskWithStatusDto() {
    }

    public DeskWithStatusDto(DeskResponseDto desk, ResourceStatus calculatedStatus, String reservedBy) {
        this.desk = desk;
        this.calculatedStatus = calculatedStatus;
        this.reservedBy = reservedBy;
        this.reservationId = null;
    }

    public DeskWithStatusDto(DeskResponseDto desk, ResourceStatus calculatedStatus, String reservedBy, Long reservationId) {
        this.desk = desk;
        this.calculatedStatus = calculatedStatus;
        this.reservedBy = reservedBy;
        this.reservationId = reservationId;
    }

    public DeskResponseDto getDesk() {
        return desk;
    }

    public void setDesk(DeskResponseDto desk) {
        this.desk = desk;
    }

    public ResourceStatus getCalculatedStatus() {
        return calculatedStatus;
    }

    public void setCalculatedStatus(ResourceStatus calculatedStatus) {
        this.calculatedStatus = calculatedStatus;
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
