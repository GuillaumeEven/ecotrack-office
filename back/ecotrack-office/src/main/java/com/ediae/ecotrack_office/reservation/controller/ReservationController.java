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

    // --- ENDPOINTS PARA CUALQUIER USUARIO IDENTIFICADO ---

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

    //TODO: ¿POR QUÉ AQUÍ NO TENEMOS AUTH??

    // @GetMapping("/floor/{id}/date/{date}")
    // public ResponseEntity <List <ReservationResponseDto>> getReservationsByFloorIdAndDate (@PathVariable Long id, @PathVariable String date) {

    //     List <ReservationModel> models = service.getReservationsByFloorIdAndDate(id, date);
    //     List <ReservationResponseDto> dtos = new ArrayList <>();
    //     for (ReservationModel model : models) {
    //         dtos.add(ReservationMapper.toResponseDto(model));
    //     }
    //     return ResponseEntity.ok(dtos);
    // }

    @GetMapping("/{id}")
    public ResponseEntity <ReservationResponseDto> getReservationById (Authentication auth, @PathVariable Long id) {

        ReservationModel model = service.getReservationById(id);
        return ResponseEntity.ok(ReservationMapper.toResponseDto(model));
    }

    //TODO: ¿AQUÍ TAMBIÉN HARÍA FALTA AUTH NO?
    @PostMapping
    public ReservationResponseDto createReservation (@RequestBody ReservationCreateDto dto) {

        System.out.println("Received DTO: " + dto);
        System.out.println("Date: " + dto.getDate() + " Type: " + dto.getDate().getClass());
        System.out.println("UserId: " + dto.getUserId());
        System.out.println("ResourceId: " + dto.getResourceId());
        System.out.println("Status: " + dto.getStatus());

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

    // --- ENDPOINTS SOLO PARA ADMIN Y TECNICOS

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
