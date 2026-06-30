package com.ediae.ecotrack_office.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ediae.ecotrack_office.organization.dto.OrganizationCreateDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationResponseDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationUpdateDto;
import com.ediae.ecotrack_office.organization.mapper.OrganizationMapper;
import com.ediae.ecotrack_office.organization.service.OrganizationService;
import com.ediae.ecotrack_office.shared.guard.AdminGuard;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private UserService usrService;

    @Autowired
    private AdminGuard adminGuard;

    // --- Endpoints públicos ---

    @PostMapping("/public/create")
    public ResponseEntity<OrganizationResponseDto> createOrganization (@Valid @RequestBody OrganizationCreateDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrganizationMapper.toResponseDto(orgService.createOrganization(dto)));
    }

    // --- Enpoints privados ---

    @GetMapping
    public ResponseEntity<OrganizationResponseDto> getOrganizationById (Authentication auth) {

        adminGuard.requireAdmin(auth);
        Long userId = (Long) auth.getPrincipal();
        UserModel user = usrService.getUserById(userId);

        return ResponseEntity.ok(OrganizationMapper.toResponseDto(orgService.getOrganizationById(user.getOrganizationId())));
    }

    @PutMapping("/update")
    public ResponseEntity<OrganizationResponseDto> updateOrganization (Authentication auth, @Valid @RequestBody OrganizationUpdateDto dto) {

        adminGuard.requireAdmin(auth);
        Long userId = (Long) auth.getPrincipal();
        UserModel user = usrService.getUserById(userId);

        return ResponseEntity.ok(OrganizationMapper.toResponseDto(orgService.updateOrganizationById(user.getOrganizationId(), dto)));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<OrganizationResponseDto> deactivateOrganization (Authentication auth) {

        adminGuard.requireAdmin(auth);
        Long userId = (Long) auth.getPrincipal();
        UserModel user = usrService.getUserById(userId);

        return ResponseEntity.ok(OrganizationMapper.toResponseDto(orgService.deactivateOrganization(user.getOrganizationId())));
    }

    @DeleteMapping("/delete")
    public Boolean deleteOrganization (Authentication auth) {

        adminGuard.requireAdmin(auth);
        Long userId = (Long) auth.getPrincipal();
        UserModel user = usrService.getUserById(userId);

        return orgService.deleteOrganization(user.getOrganizationId());
    }



}
