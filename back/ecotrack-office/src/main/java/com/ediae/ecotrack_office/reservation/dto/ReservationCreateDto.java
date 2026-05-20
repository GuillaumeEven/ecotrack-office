package com.ediae.ecotrack_office.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.users.entity.UserEntity;

public class ReservationCreateDto {

    // Atributos

    private LocalDate date;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private UserEntity user;
    private ResourceEntity resource;

    // Constructores

    public ReservationCreateDto () {}

    public ReservationCreateDto (LocalDate date, ReservationStatus status, LocalDateTime createdAt, UserEntity user, ResourceEntity resource) {
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
        this.resource = resource;
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

    public LocalDateTime getCreatedAt () {

        return this.createdAt;
    }
    public void setCreateAt (LocalDateTime createAt) {

        this.createdAt = createAt;
    }

    public UserEntity getUser () {

        return this.user;
    }
    public void setUser (UserEntity user) {

        this.user = user;
    }

    public ResourceEntity getResource () {

        return this.resource;
    }
    public void setResource (ResourceEntity resource) {

        this.resource = resource;
    }
}
