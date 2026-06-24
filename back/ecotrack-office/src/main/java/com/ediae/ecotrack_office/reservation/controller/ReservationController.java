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
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.mapper.ReservationMapper;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.reservation.service.ReservationService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;
import com.ediae.ecotrack_office.users.enums.Role;

@RestController
// @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
//     RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
// })
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping("/user/{id}")
    public List <ReservationResponseDto> getReservationsByUserId (@PathVariable Long id) {

        List <ReservationModel> models = service.getReservationsByUserId(id);
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {

            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return dtos;
    }

    @GetMapping("/floor/{id}/date/{date}")
    public ResponseEntity <List <ReservationResponseDto>> getReservationsByFloorIdAndDate (@PathVariable Long id, @PathVariable String date) {

        List <ReservationModel> models = service.getReservationsByFloorIdAndDate(id, date);
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {
            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public List <ReservationResponseDto> getAllReservations () {

        List <ReservationModel> models = service.getAllReservations();
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {

            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public ReservationResponseDto getReservationById (@PathVariable Long id) {

        ReservationModel model = service.getReservationById(id);
        return ReservationMapper.toResponseDto(model);
    }

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
    public ReservationResponseDto updateReservation (@PathVariable Long id, @RequestBody ReservationUpdateDto dto) {

        return ReservationMapper.toResponseDto(service.updateReservationById(id, dto));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteReservation (@PathVariable Long id, Authentication auth) {
        Long currentUserId = (Long) auth.getPrincipal();
        boolean isAdminOrTech = roleGuard.hasAnyRole(auth, Role.ADMIN, Role.TECHNICIAN);

        // If not admin/tech, verify it's the user's own reservation
        if (!isAdminOrTech) {
            // User can only delete their own reservations
            ReservationModel reservation = service.getReservationById(id);
            if (!reservation.getUser().getId().equals(currentUserId)) {
                throw new com.ediae.ecotrack_office.shared.exception.ForbiddenException("No tienes permisos para realizar esta acción.");
            }
        }

        return service.deleteReservationById(id, currentUserId, isAdminOrTech);
    }
}
