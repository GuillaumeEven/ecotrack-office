package com.ediae.ecotrack_office.assets.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


@RestController
@RequestMapping("/api/v1/floors")
public class FloorController {

    @Autowired
    private FloorService floorService;

    @Autowired
    private FloorMapper floorMapper;

    @Autowired
    private ResourceStatusCalculatorService resourceStatusCalculatorService;

    @GetMapping
    public ResponseEntity<List<FloorResponseDto>> getAllFloors() {
        List<FloorResponseDto> floors = new ArrayList<>();
        floorService.getFloors().forEach(floor -> floors.add(floorMapper.toResponseDto(floor)));
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FloorResponseDto> getFloorById(@PathVariable Long id) {
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.getFloorById(id));
        return ResponseEntity.ok(floor);
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<FloorResponseDto>> getFloorsByOrganizationId(@PathVariable Long organizationId) {
        List<FloorResponseDto> floors = new ArrayList<>();
        floorService.getFloorsByOrganizationId(organizationId).forEach(floor -> floors.add(floorMapper.toResponseDto(floor)));
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<List<FloorWithStatusDto>> getFloorsStatusByOrganizationId(@PathVariable Long id, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<FloorWithStatusDto> floorsWithStatus = resourceStatusCalculatorService.calculateFloorsStatusForDate(id, localDate);
        return ResponseEntity.ok(floorsWithStatus);
    }

    @PostMapping
    public ResponseEntity<FloorResponseDto> createFloor(@RequestBody FloorRequestDto floorRequestDto) {
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.createFloor(floorRequestDto));
        return ResponseEntity.ok(floor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FloorResponseDto> updateFloor(@PathVariable Long id, @RequestBody FloorRequestDto floorRequestDto) {
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.updateFloor(id, floorRequestDto));
        return ResponseEntity.ok(floor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFloor(@PathVariable Long id) {
        floorService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }
}
