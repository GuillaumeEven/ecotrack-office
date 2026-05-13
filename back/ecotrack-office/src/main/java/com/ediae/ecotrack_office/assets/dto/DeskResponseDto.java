package com.ediae.ecotrack_office.assets.dto;

public class DeskResponseDto extends ResourceResponseDto {

    private Long roomId;

    public DeskResponseDto() {
    }

    public DeskResponseDto(
        Long id,
        String name,
        String status,
        Boolean isActive,
        String equipmentList,
        Long roomId
    ) {
        super(id, name, status, isActive, equipmentList);
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

}
