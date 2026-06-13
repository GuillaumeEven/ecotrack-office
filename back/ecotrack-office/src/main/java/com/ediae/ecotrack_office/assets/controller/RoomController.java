package com.ediae.ecotrack_office.assets.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.assets.dto.RoomRequestDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.service.RoomService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;


@RestController
@RequestMapping("api/v1/rooms")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.GET,
    RequestMethod.POST,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.OPTIONS}
)
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getRooms(Authentication auth) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        List<RoomResponseDto> rooms = new ArrayList<>();
        roomService.getRooms().forEach(room -> rooms.add(roomMapper.toResponseDto(room)));
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(Authentication auth, @PathVariable Long id) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.getRoomById(id));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @GetMapping("/floor/{floorId}")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByFloorId(Authentication auth, @PathVariable Long floorId) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        List<RoomResponseDto> rooms = new ArrayList<>();
        roomService.getRoomsByFloorId(floorId).forEach(room -> rooms.add(roomMapper.toResponseDto(room)));
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("")
    public ResponseEntity<RoomResponseDto> createRoom(Authentication auth, @RequestBody RoomRequestDto roomRequestDTO) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = roleGuard.getUserIdFromAuth(auth);
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.createRoom(roomRequestDTO));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(Authentication auth, @PathVariable Long id, @RequestBody RoomRequestDto roomRequestDTO) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = roleGuard.getUserIdFromAuth(auth);
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.updateRoom(id, roomRequestDTO));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}

