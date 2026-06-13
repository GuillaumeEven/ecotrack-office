package com.ediae.ecotrack_office.assets.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.dto.FloorWithStatusDto;
import com.ediae.ecotrack_office.assets.mapper.FloorMapper;
import com.ediae.ecotrack_office.assets.service.FloorService;
import com.ediae.ecotrack_office.assets.service.ResourceStatusCalculatorService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;


@RestController
@RequestMapping("/api/v1/floors")
public class FloorController {

    @Autowired
    private FloorService floorService;

    @Autowired
    private FloorMapper floorMapper;

    @Autowired
    private ResourceStatusCalculatorService resourceStatusCalculatorService;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping
    public ResponseEntity<List<FloorResponseDto>> getAllFloors(Authentication auth) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        List<FloorResponseDto> floors = new ArrayList<>();
        floorService.getFloors().forEach(floor -> floors.add(floorMapper.toResponseDto(floor)));
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FloorResponseDto> getFloorById(Authentication auth, @PathVariable Long id) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.getFloorById(id));
        return ResponseEntity.ok(floor);
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<FloorResponseDto>> getFloorsByOrganizationId(Authentication auth, @PathVariable Long organizationId) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        List<FloorResponseDto> floors = new ArrayList<>();
        floorService.getFloorsByOrganizationId(organizationId).forEach(floor -> floors.add(floorMapper.toResponseDto(floor)));
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<FloorWithStatusDto> getFloorStatus(Authentication auth, @PathVariable Long id, @RequestParam String date) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        LocalDate localDate = LocalDate.parse(date);
        FloorWithStatusDto floorWithStatus = resourceStatusCalculatorService.getFloorWithStatusForDate(id, localDate);
        return ResponseEntity.ok(floorWithStatus);
    }

    @PostMapping
    public ResponseEntity<FloorResponseDto> createFloor(Authentication auth, @RequestBody FloorRequestDto floorRequestDto) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = roleGuard.getUserIdFromAuth(auth);
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.createFloor(floorRequestDto));
        return ResponseEntity.ok(floor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FloorResponseDto> updateFloor(Authentication auth, @PathVariable Long id, @RequestBody FloorRequestDto floorRequestDto) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = roleGuard.getUserIdFromAuth(auth);
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.updateFloor(id, floorRequestDto));
        return ResponseEntity.ok(floor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFloor(Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        floorService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }
}
