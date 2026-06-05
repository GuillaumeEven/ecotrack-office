package com.ediae.ecotrack_office.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT r FROM ReservationEntity r WHERE r.user.id = :userId")
    List<ReservationEntity> findByUserId(Long userId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.resource.id = :resourceId")
    List<ReservationEntity> findByResourceId(Long resourceId);

    // Counts active reservations for desks belonging to a given room on a given date.
    // Used to compute occupancy rate for zone consolidation logic.
    @Query("SELECT COUNT(r) FROM ReservationEntity r " +
           "JOIN DeskEntity d ON d.id = r.resource.id " +
           "WHERE d.room.id = :roomId " +
           "AND r.date = :date " +
           "AND r.status NOT IN (:excludedStatuses)")
    long countActiveReservationsForRoom(Long roomId, LocalDate date, List<ReservationStatus> excludedStatuses);
}
