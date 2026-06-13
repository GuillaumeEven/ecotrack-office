package com.ediae.ecotrack_office.organization.controller;

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

import com.ediae.ecotrack_office.organization.dto.OrganizationCreateDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationResponseDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationUpdateDto;
import com.ediae.ecotrack_office.organization.mapper.OrganizationMapper;
import com.ediae.ecotrack_office.organization.service.OrganizationService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET,
        RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService service;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping("/{id}")
    public OrganizationResponseDto getOrganizationById (Authentication auth, @PathVariable Long id) {

        return OrganizationMapper.toResponseDto(service.getOrganizationById(id));
    }

    @PostMapping
    public ResponseEntity<OrganizationResponseDto> createOrganization (Authentication auth, @RequestBody OrganizationCreateDto dto) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        System.out.println("La dirección recibida es: " + dto.getAddress());
        return ResponseEntity.ok(OrganizationMapper.toResponseDto(service.createOrganization(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponseDto> updateOrganization (Authentication auth, @PathVariable Long id, @RequestBody OrganizationUpdateDto dto) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(OrganizationMapper.toResponseDto(service.updateOrganizationById(id, dto)));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<OrganizationResponseDto> deactivateOrganization (Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(OrganizationMapper.toResponseDto(service.deactivateOrganization(id)));
    }

    @DeleteMapping("/{id}") //TENER EN CUENTA QUE PARA BORRAR UNA ORGANIZACIÓN PRIMERO HABRÍA QUE BORRAR LOS USUARIO ASOCIADOS A ELLA, Y ESTO GENERA UNA ELIMINACIÓN DE ELEMENTOS EN CADENA.
    public ResponseEntity<Boolean> deleteOrganization (Authentication auth, @PathVariable Long id) {
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(service.deleteOrganization(id));
    }



}
