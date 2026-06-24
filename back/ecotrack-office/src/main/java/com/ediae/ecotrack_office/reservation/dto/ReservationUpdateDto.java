package com.ediae.ecotrack_office.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.users.entity.UserEntity;

public class ReservationUpdateDto {

    // Atributos

    private LocalDate date;
    private ReservationStatus status;
    private Long userId;

    // Constructores

    public ReservationUpdateDto () {}

    public ReservationUpdateDto (LocalDate date, ReservationStatus status, Long userId) {
        this.date = date;
        this.status = status;
        this.userId = userId;
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
    public void setUser (Long userId) {

        this.userId = userId;
    }
}
