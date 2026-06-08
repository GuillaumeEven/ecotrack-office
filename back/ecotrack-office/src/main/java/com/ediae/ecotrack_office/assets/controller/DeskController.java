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

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.assets.dto.DeskResponseDto;
import com.ediae.ecotrack_office.assets.mapper.DeskMapper;
import com.ediae.ecotrack_office.assets.service.DeskService;

@RestController
@RequestMapping("api/v1/desks")
@CrossOrigin(origins = "*")
public class DeskController {

    @Autowired
    private DeskService deskService;

    @Autowired
    private DeskMapper deskMapper;

    @GetMapping("/{id}")
    public ResponseEntity<DeskResponseDto> getDeskById(@PathVariable Long id) {
        DeskResponseDto deskResponseDTO = deskMapper.toResponseDto(deskService.getDeskById(id));
        return ResponseEntity.ok(deskResponseDTO);
    }

    @PostMapping("")
    public ResponseEntity<DeskResponseDto> createDesk(@RequestBody DeskRequestDto deskRequestDTO) {
        DeskResponseDto deskResponseDTO = deskMapper.toResponseDto(deskService.createDesk(deskRequestDTO));
        return ResponseEntity.ok(deskResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeskResponseDto> updateDesk(@PathVariable Long id, @RequestBody DeskRequestDto deskRequestDTO) {
        DeskResponseDto deskResponseDTO = deskMapper.toResponseDto(deskService.updateDesk(id, deskRequestDTO));
        return ResponseEntity.ok(deskResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesk(@PathVariable Long id) {
        deskService.deleteDeskById(id);
        return ResponseEntity.noContent().build();
    }
}
