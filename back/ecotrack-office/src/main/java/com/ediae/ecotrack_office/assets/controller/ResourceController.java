package com.ediae.ecotrack_office.assets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.assets.dto.DeskResponseDto;
import com.ediae.ecotrack_office.assets.dto.RoomResponseDto;
import com.ediae.ecotrack_office.assets.mapper.DeskMapper;
import com.ediae.ecotrack_office.assets.mapper.RoomMapper;
import com.ediae.ecotrack_office.assets.model.DeskModel;
import com.ediae.ecotrack_office.assets.model.ResourceModel;
import com.ediae.ecotrack_office.assets.model.RoomModel;
import com.ediae.ecotrack_office.assets.service.ResourceService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleGuard roleGuard;

    @Autowired
    private DeskMapper deskMapper;

    @Autowired
    private RoomMapper roomMapper;

    @GetMapping("/{resourceId}")
    public ResponseEntity<?> getResourceById(@PathVariable Long resourceId) {
        ResourceModel resourceModel = resourceService.getResourceById(resourceId);

        if (resourceModel instanceof DeskModel deskModel) {
            DeskResponseDto responseDto = deskMapper.toResponseDto(deskModel);
            return ResponseEntity.ok(responseDto);
        } else if (resourceModel instanceof RoomModel roomModel) {
            RoomResponseDto responseDto = roomMapper.toResponseDto(roomModel);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.badRequest().body("Unsupported resource type");
        }
    }

}
