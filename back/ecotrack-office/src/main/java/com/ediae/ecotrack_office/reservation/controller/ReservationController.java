package com.ediae.ecotrack_office.reservation.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
})
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    @GetMapping("/user/{id}")
    public List <ReservationResponseDto> getReservationsByUserId (@PathVariable Long id) {

        List <ReservationModel> models = service.getReservationsByUserId(id);
        List <ReservationResponseDto> dtos = new ArrayList <>();
        for (ReservationModel model : models) {

            dtos.add(ReservationMapper.toResponseDto(model));
        }
        return dtos;
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

        return ReservationMapper.toResponseDto(service.createReservation(dto));
    }

    @PutMapping("/{id}")
    public ReservationResponseDto updateReservation (@PathVariable Long id, @RequestBody ReservationUpdateDto dto) {

        return ReservationMapper.toResponseDto(service.updateReservationById(id, dto));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteReservation (@PathVariable Long id) {

        return service.deleteReservationById(id);
    }
}
