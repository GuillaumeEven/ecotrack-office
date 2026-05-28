package com.ediae.ecotrack_office.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {


    @Query("SELECT r FROM ReservationEntity r WHERE r.user.id = :userId")
    List <ReservationEntity> findByUserId (Long userId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.resource.id = :resourceId")
    List <ReservationEntity> findByResourceId (Long resourceId);
}
