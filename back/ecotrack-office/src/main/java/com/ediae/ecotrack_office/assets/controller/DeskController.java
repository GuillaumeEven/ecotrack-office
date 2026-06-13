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

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.assets.dto.DeskResponseDto;
import com.ediae.ecotrack_office.assets.mapper.DeskMapper;
import com.ediae.ecotrack_office.assets.service.DeskService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;

@RestController
@RequestMapping("api/v1/desks")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.GET,
    RequestMethod.POST,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.OPTIONS}
)
public class DeskController {

    @Autowired
    private DeskService deskService;

    @Autowired
    private DeskMapper deskMapper;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping("/{id}")
    public ResponseEntity<DeskResponseDto> getDeskById(Authentication auth, @PathVariable Long id) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        DeskResponseDto deskResponseDTO = deskMapper.toResponseDto(deskService.getDeskById(id));
        return ResponseEntity.ok(deskResponseDTO);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<DeskResponseDto>> getDesksByRoomId(Authentication auth, @PathVariable Long roomId) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        List<DeskResponseDto> desks = new ArrayList<>();
        deskService.getDesksByRoomId(roomId).forEach(desk -> desks.add(deskMapper.toResponseDto(desk)));
        return ResponseEntity.ok(desks);
    }

    @PostMapping("")
    public ResponseEntity<DeskResponseDto> createDesk(Authentication auth, @RequestBody DeskRequestDto deskRequestDTO) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = roleGuard.getUserIdFromAuth(auth);
        DeskResponseDto deskResponseDTO = deskMapper.toResponseDto(deskService.createDesk(deskRequestDTO));
        return ResponseEntity.ok(deskResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeskResponseDto> updateDesk(Authentication auth, @PathVariable Long id, @RequestBody DeskRequestDto deskRequestDTO) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = roleGuard.getUserIdFromAuth(auth);
        DeskResponseDto deskResponseDTO = deskMapper.toResponseDto(deskService.updateDesk(id, deskRequestDTO));
        return ResponseEntity.ok(deskResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesk(Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        deskService.deleteDeskById(id);
        return ResponseEntity.noContent().build();
    }
}
