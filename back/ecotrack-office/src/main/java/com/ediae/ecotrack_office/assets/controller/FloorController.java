package com.ediae.ecotrack_office.assets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.assets.dto.FloorResponseDto;
import com.ediae.ecotrack_office.assets.mapper.FloorMapper;
import com.ediae.ecotrack_office.assets.service.FloorService;


@RestController
@RequestMapping("/api/v1/floors")
public class FloorController {

    @Autowired
    private FloorService floorService;

    @Autowired
    private FloorMapper floorMapper;

    @GetMapping("/{id}")
    public ResponseEntity<FloorResponseDto> getFloorById(@PathVariable Long id) {
        FloorResponseDto floor = floorMapper.toResponseDto(floorService.getFloorById(id));
        return ResponseEntity.ok(floor);
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
