package com.ediae.ecotrack_office.assets.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.ediae.ecotrack_office.shared.exception.ErrorResponse;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;
import com.ediae.ecotrack_office.users.enums.Role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("api/v1/rooms")
@Tag(name = "Rooms", description = "Gestión de salas/oficinas")
public class RoomController {

    @Autowired
    private RoleGuard roleGuard;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomMapper roomMapper;

    @GetMapping
    @Operation(summary = "Obtener todas las salas", description = "Recuperar todas las salas disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de salas obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoomResponseDto>> getRooms() {
        List<RoomResponseDto> rooms = new ArrayList<>();
        roomService.getRooms().forEach(room -> rooms.add(roomMapper.toResponseDto(room)));
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sala por ID", description = "Recuperar una sala específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sala encontrada"),
        @ApiResponse(responseCode = "404", description = "Sala no encontrada",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.getRoomById(id));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @GetMapping("/floor/{floorId}")
    @Operation(summary = "Obtener salas por piso", description = "Recuperar todas las salas en un piso específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de salas obtenida"),
        @ApiResponse(responseCode = "404", description = "Piso no encontrado",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoomResponseDto>> getRoomsByFloorId(@PathVariable Long floorId) {
        List<RoomResponseDto> rooms = new ArrayList<>();
        roomService.getRoomsByFloorId(floorId).forEach(room -> rooms.add(roomMapper.toResponseDto(room)));
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("")
    @Operation(summary = "Crear nueva sala", description = "Crear una nueva sala (solo ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sala creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto roomRequestDTO, Authentication auth) {
        roleGuard.requireAnyRole(auth, Role.ADMIN);
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.createRoom(roomRequestDTO));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sala", description = "Actualizar una sala existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sala actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Sala no encontrada",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoomResponseDto> updateRoom(@PathVariable Long id, @RequestBody RoomRequestDto roomRequestDTO) {
        RoomResponseDto roomResponseDTO = roomMapper.toResponseDto(roomService.updateRoom(id, roomRequestDTO));
        return ResponseEntity.ok(roomResponseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sala", description = "Eliminar una sala por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sala eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Sala no encontrada",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}

