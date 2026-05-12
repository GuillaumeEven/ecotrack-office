package com.ediae.ecotrack_office.assets.dto;


public class DeskRequestDto extends ResourceRequestDto {

    private Long roomId;

    public DeskRequestDto() {
    }

    public DeskRequestDto(
        String name,
        String status,
        Boolean isActive,
        String equipmentList,
        Long roomId
    ) {
        super(name, status, isActive, equipmentList);
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

}
