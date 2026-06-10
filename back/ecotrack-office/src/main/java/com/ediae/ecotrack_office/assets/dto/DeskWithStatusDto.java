package com.ediae.ecotrack_office.assets.dto;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

public class DeskWithStatusDto {

    private DeskResponseDto desk;
    private ResourceStatus calculatedStatus;
    private String reservedBy;

    public DeskWithStatusDto() {
    }

    public DeskWithStatusDto(DeskResponseDto desk, ResourceStatus calculatedStatus, String reservedBy) {
        this.desk = desk;
        this.calculatedStatus = calculatedStatus;
        this.reservedBy = reservedBy;
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

}
