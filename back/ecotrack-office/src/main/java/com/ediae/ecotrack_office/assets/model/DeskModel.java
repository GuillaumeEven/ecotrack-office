package com.ediae.ecotrack_office.assets.model;

import com.ediae.ecotrack_office.assets.enums.ResourceStatus;


public class DeskModel extends ResourceModel {

    private Long roomId;

    public DeskModel() {
    }

    public DeskModel(
        Long id,
        String name,
        ResourceStatus status,
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
