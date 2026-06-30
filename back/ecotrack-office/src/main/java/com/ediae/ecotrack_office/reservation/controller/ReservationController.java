package com.ediae.ecotrack_office.reservation.controller;

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

import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationResponseDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationResponseWithNameDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.mapper.ReservationMapper;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.reservation.service.ReservationService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;
import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.service.UserService;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping("/user")
    public ResponseEntity <List <ReservationResponseDto>> getReservationsByUserId (Authentication auth) {

        Long userId = (Long) auth.getPrincipal();
        List <ReservationModel> models = service.getReservationsByUserId(userId);
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {

            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity <ReservationResponseDto> getReservationById (Authentication auth, @PathVariable Long id) {

        ReservationModel model = service.getReservationById(id);
        return ResponseEntity.ok(ReservationMapper.toResponseDto(model));
    }

    @PostMapping
    public ReservationResponseDto createReservation (Authentication auth, @RequestBody ReservationCreateDto dto) {

        return ReservationMapper.toResponseDto(service.createReservation(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity <ReservationResponseDto> updateReservation (Authentication auth, @PathVariable Long id, @RequestBody ReservationUpdateDto dto) {

        boolean isAdminOrTech = roleGuard.hasAnyRole(auth, Role.ADMIN, Role.TECHNICIAN);
        Long userId = (Long) auth.getPrincipal();
        if(!isAdminOrTech) {

            ReservationModel reservation = service.getReservationById(id);
            if(!reservation.getUser().getId().equals(userId)) {

                throw new com.ediae.ecotrack_office.shared.exception.ForbiddenException("No tienes permisos para realizar esta acción.");
            }
        }

        return ResponseEntity.ok(ReservationMapper.toResponseDto(service.updateReservationById(id, dto)));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteReservation (@PathVariable Long id, Authentication auth) {
        Long currentUserId = (Long) auth.getPrincipal();
        return service.deleteReservationById(id, currentUserId);
    }

    // -- Endpoints solo para admin y tecnicos

    @GetMapping("/all")
    public ResponseEntity <List <ReservationResponseWithNameDto>> getAllReservationsFromTheOranizationUser (Authentication auth) {

        boolean isAdminOrTech = roleGuard.hasAnyRole(auth, Role.ADMIN, Role.TECHNICIAN);

        if(isAdminOrTech) {
            Long userId = (Long) auth.getPrincipal();
            UserModel user = userService.getUserById(userId);
            List <ReservationModel> models = service.getAllReservationsByOrganizationId(user.getOrganizationId());
            List <ReservationResponseWithNameDto> dtos = new ArrayList <>();
            for (ReservationModel model : models) {

                dtos.add(ReservationMapper.toResponseWithNameDto(model));
            }
            return ResponseEntity.ok(dtos);
        } else {

            throw new com.ediae.ecotrack_office.shared.exception.ForbiddenException("No tienes permisos para realizar esta acción.");
        }
    }
}
