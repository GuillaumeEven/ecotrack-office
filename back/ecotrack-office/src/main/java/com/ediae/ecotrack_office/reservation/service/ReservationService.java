package com.ediae.ecotrack_office.reservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.reservation.mapper.ReservationMapper;
import com.ediae.ecotrack_office.reservation.model.ReservationModel;
import com.ediae.ecotrack_office.reservation.repository.ReservationRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;

    public List <ReservationModel> getReservationsByUserId (Long userId) {

        List <ReservationEntity> entities = repository.findByUserId(userId);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public List <ReservationModel> getReservationsByResourceId (Long resourceId) {

        List <ReservationEntity> entities = repository.findByResourceId(resourceId);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public List <ReservationModel> getReservationsByFloorIdAndDate (Long floorId, String date) {

        LocalDate localDate = LocalDate.parse(date);
        List <ReservationEntity> entities = repository.findByFloorIdAndDate(floorId, localDate);
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public List <ReservationModel> getAllReservations () {

        List <ReservationEntity> entities = repository.findAll();
        List <ReservationModel> models = new ArrayList <>();
        for (ReservationEntity entity : entities) {

            models.add(ReservationMapper.fromEntity(entity));
        }
        return models;
    }

    public ReservationModel getReservationById (Long id) {

        Optional <ReservationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una reserva con id: " + id);
        }
        return ReservationMapper.fromEntity(entity.get());
    }

    public ReservationModel createReservation (ReservationCreateDto dto) { // RECORDAR QUE HAY QUE CAMBIAR EL ESTATUS DE LO QUE ESTÁS RESERVANDO CON EL SERVICIO DE RESOURCE

        ReservationModel model = ReservationMapper.fromCreateDto(dto);
        ReservationEntity entity = ReservationMapper.toEntity(model);
        entity.setStatus(ReservationStatus.RELEASED);
        repository.save(entity);
        return ReservationMapper.fromEntity(entity);
    }

    public ReservationModel updateReservationById (Long id, ReservationUpdateDto dto) {

        Optional <ReservationEntity> initialEntity = repository.findById(id);
        if(initialEntity.isEmpty()) {

            throw new RuntimeException("No se ha econtrado una reserva con id: " + id);
        }
        ReservationModel model = ReservationMapper.fromUpdateDto(dto);
        ReservationEntity savedEntity = repository.save(ReservationMapper.toEntity(model));
        return ReservationMapper.fromEntity(savedEntity);
    }

    public Boolean deleteReservationById (Long id) {

        Optional <ReservationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una reserva con id: " + id);
        }
        repository.deleteById(id);
        if (repository.findById(id).isEmpty()) return true;
        else return false;
    }
}
