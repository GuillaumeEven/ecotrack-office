package com.ediae.ecotrack_office.assets.service;

import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;

@Service
public interface RoomService {

    public RoomResponseDto getRoomById(Long roomId);
    public RoomResponseDto createRoom(RoomRequestDto roomRequestDTO);
    public RoomResponseDto updateRoom(Long roomId, RoomRequestDto roomRequestDTO);
    public void deleteRoomById(Long roomId);

}
