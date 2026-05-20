package com.ediae.ecotrack_office.reservation.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationResponseDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;

@Component
public class ReservationMapper {

    public ReservationModel fromEntity (ReservationEntity entity) {

        ReservationModel model = new ReservationModel();
        model.setId(entity.getId());
        model.setDate(entity.getDate());
        model.setStatus(entity.getStatus());
        model.setCreateAt(entity.getCreatedAt());
        model.setUser(entity.getUser());
        model.setResource(entity.getResource());
        return model;
    }

    public ReservationEntity toEntity (ReservationModel model) {

        ReservationEntity entity = new ReservationEntity();
        entity.setId(model.getId());
        entity.setDate(model.getDate());
        entity.setStatus(model.getStatus());
        entity.setCreateAt(model.getCreatedAt());
        entity.setUser(model.getUser());
        entity.setResource(model.getResource());
        return entity;
    }

    public ReservationModel fromCreatDto (ReservationCreateDto createDto) {

        ReservationModel model = new ReservationModel();
        model.setId(null);
        model.setDate(createDto.getDate());
        model.setStatus(createDto.getStatus());
        model.setCreateAt(createDto.getCreatedAt());
        model.setUser(createDto.getUser());
        model.setResource(createDto.getResource());
        return model;
    }

    public ReservationModel fromUpdateDto (ReservationUpdateDto updateDto) {

        ReservationModel model = new ReservationModel();
        model.setId(updateDto.getId());
        model.setDate(updateDto.getDate());
        model.setStatus(updateDto.getStatus());
        model.setCreateAt(updateDto.getCreatedAt());
        model.setUser(updateDto.getUser());
        model.setResource(updateDto.getResource());
        return model;
    }

    public ReservationResponseDto toResponseDto (ReservationModel model) {

        ReservationResponseDto response = new ReservationResponseDto();
        response.setId(model.getId());
        response.setDate(model.getDate());
        response.setStatus(model.getStatus());
        response.setCreateAt(model.getCreatedAt());
        response.setUser(model.getUser());
        response.setResource(model.getResource());
        return response;
    }
}
