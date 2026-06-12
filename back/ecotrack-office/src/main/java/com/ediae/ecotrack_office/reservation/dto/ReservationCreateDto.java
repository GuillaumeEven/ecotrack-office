package com.ediae.ecotrack_office.reservation.dto;

import java.time.LocalDate;

import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;

public class ReservationCreateDto {

    // Atributos

    private LocalDate date;
    private ReservationStatus status;
    private Long userId;
    private Long resourceId;

    // Constructores

    public ReservationCreateDto () {}

    public ReservationCreateDto (LocalDate date, ReservationStatus status, Long userId, Long resourceId) {
        this.date = date;
        this.status = status;
        this.userId = userId;
        this.resourceId = resourceId;
    }

    // Getter y Setter

    public LocalDate getDate () {

        return this.date;
    }
    public void setDate (LocalDate date) {

        this.date = date;
    }

    public ReservationStatus getStatus () {

        return this.status;
    }
    public void setStatus (ReservationStatus status) {

        this.status = status;
    }

    public Long getUserId () {

        return this.userId;
    }
    public void setUserId (Long userId) {

        this.userId = userId;
    }

    public Long getResourceId () {

        return this.resourceId;
    }
    public void setResourceId (Long resourceId) {

        this.resourceId = resourceId;
    }
}
