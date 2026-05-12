package com.ediae.ecotrack_office.assets.dto;

public class RoomRequestDto extends ResourceRequestDto {

    private String roomType;
    private Double surfaceArea;
    private Long floorId;
    private Integer capacity;

    public RoomRequestDto() {
    }

    public RoomRequestDto(
        String name,
        String status,
        Boolean isActive,
        String equipmentList,
        String roomType,
        Double surfaceArea,
        Long floorId,
        Integer capacity
    ) {
        super(name, status, isActive, equipmentList);
        this.roomType = roomType;
        this.surfaceArea = surfaceArea;
        this.floorId = floorId;
        this.capacity = capacity;
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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

}
