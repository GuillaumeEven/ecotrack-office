package com.ediae.ecotrack_office.assets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.service.RoomService;


@RestController
@RequestMapping("api/v1/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomMapper roomMapper;

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.getRoomById(id));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @PostMapping("")
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto roomRequestDTO) {
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.createRoom(roomRequestDTO));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(@PathVariable Long id, @RequestBody RoomRequestDto roomRequestDTO) {
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.updateRoom(id, roomRequestDTO));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}

