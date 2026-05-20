package com.ediae.ecotrack_office.assets.service;

import java.util.List;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.model.RoomModel;

public interface RoomService {

    public RoomModel getRoomById(Long roomId);

    public RoomModel createRoom(RoomRequestDto roomRequestDTO);

    public RoomModel updateRoom(Long roomId, RoomRequestDto roomRequestDTO);

    public void deleteRoomById(Long roomId);

    public List<RoomModel> getRoomsByFloorId(Long floorId);

}
