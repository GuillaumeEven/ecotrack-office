package com.ediae.ecotrack_office.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;

public record ReservationResponseWithNameDto(
    Long id,
    LocalDate date,
    ReservationStatus status,
    LocalDateTime createdAt,
    Long userId,
    String userFullName,
    String resourceName,
    String resourceEquipmentList
) {}
