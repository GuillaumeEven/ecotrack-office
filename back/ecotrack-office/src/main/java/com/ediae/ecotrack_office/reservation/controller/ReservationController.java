package com.ediae.ecotrack_office.reservation.controller;

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

import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationResponseDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.mapper.ReservationMapper;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.reservation.service.ReservationService;
import com.ediae.ecotrack_office.shared.guard.RoleGuard;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
})
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @Autowired
    private RoleGuard roleGuard;

    @GetMapping("/user/{id}")
    public List <ReservationResponseDto> getReservationsByUserId (Authentication auth, @PathVariable Long id) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        // Verify that the user can only see their own reservations (unless ADMIN)
        if (!userId.equals(id) && !roleGuard.isAdmin(auth)) {
            return new ArrayList<>();  // Return empty list instead of throwing exception
        }

        List <ReservationModel> models = service.getReservationsByUserId(id);
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {

            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return dtos;
    }

    @GetMapping("/floor/{id}/date/{date}")
    public ResponseEntity <List <ReservationResponseDto>> getReservationsByFloorIdAndDate (Authentication auth, @PathVariable Long id, @PathVariable String date) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        List <ReservationModel> models = service.getReservationsByFloorIdAndDate(id, date);
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {
            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public List <ReservationResponseDto> getAllReservations (Authentication auth) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        List <ReservationModel> models = service.getAllReservations();
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {

            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public ReservationResponseDto getReservationById (Authentication auth, @PathVariable Long id) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        ReservationModel model = service.getReservationById(id);
        return ReservationMapper.toResponseDto(model);
    }

    @PostMapping
    public ReservationResponseDto createReservation (Authentication auth, @RequestBody ReservationCreateDto dto) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        System.out.println("POST /reservations por userId: " + userId);
        System.out.println("Received DTO: " + dto);

        return ReservationMapper.toResponseDto(service.createReservation(dto));
    }

    @PutMapping("/{id}")
    public ReservationResponseDto updateReservation (Authentication auth, @PathVariable Long id, @RequestBody ReservationUpdateDto dto) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        return ReservationMapper.toResponseDto(service.updateReservationById(id, dto));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteReservation (Authentication auth, @PathVariable Long id) {
        Long userId = roleGuard.getUserIdFromAuth(auth);

        return service.deleteReservationById(id);
    }
}
