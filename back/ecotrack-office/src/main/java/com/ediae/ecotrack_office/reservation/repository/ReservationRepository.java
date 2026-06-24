package com.ediae.ecotrack_office.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {


    // OBTENER TODAS LAS RESERVAS DE UNA ORGANIZACIÓN
    @Query("SELECT r FROM ReservationEntity r WHERE r.user.organization.id = :organizationId")
    List <ReservationEntity> findByOrganizationId (Long organizationId);
    
    @Query("SELECT r FROM ReservationEntity r WHERE r.user.id = :userId")
    List <ReservationEntity> findByUserId (Long userId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.resource.id = :resourceId")
    List <ReservationEntity> findByResourceId (Long resourceId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.resource.room.floor.id = :floorId AND r.date = :date")
    List <ReservationEntity> findByFloorIdAndDate (Long floorId, LocalDate date);
}
