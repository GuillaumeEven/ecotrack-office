package com.ediae.ecotrack_office.reservation.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.reservation.dto.ReservationResponseDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationResponseWithNameDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.service.UserService;

    @Component
    public class ReservationMapper {

        public static ReservationModel fromEntity (ReservationEntity entity) {

            ReservationModel model = new ReservationModel();
            model.setId(entity.getId());
            model.setDate(entity.getDate());
            model.setStatus(entity.getStatus());
            model.setCreatedAt(entity.getCreatedAt());
            model.setUser(entity.getUser());
            model.setResource(entity.getResource());
            return model;
        }

        public static ReservationEntity toEntity (ReservationModel model) {

            ReservationEntity entity = new ReservationEntity();
            entity.setId(model.getId());
            entity.setDate(model.getDate());
            entity.setStatus(model.getStatus());
            entity.setCreatedAt(model.getCreatedAt());
            entity.setUser(model.getUser());
            entity.setResource(model.getResource());
            return entity;
        }

        public static ReservationResponseDto toResponseDto (ReservationModel model) {

            ReservationResponseDto response = new ReservationResponseDto();
            response.setId(model.getId());
            response.setDate(model.getDate());
            response.setStatus(model.getStatus());
            response.setCreatedAt(model.getCreatedAt());
            response.setUserId(model.getUser().getId());
            response.setResourceName(model.getResource().getName());
            response.setResourceEquipmentList(model.getResource().getEquipmentList());
            return response;
        }

        public static ReservationResponseWithNameDto toResponseWithNameDto (ReservationModel model) {

            String fullName = model.getUser().getFirstName()+" "+model.getUser().getLastName();
            ReservationResponseWithNameDto response = new ReservationResponseWithNameDto(
                model.getId(),
                model.getDate(),
                model.getStatus(),
                model.getCreatedAt(),
                model.getUser().getId(),
                fullName,
                model.getResource().getName(),
                model.getResource().getEquipmentList()
            );
            return response;
        }
    }
