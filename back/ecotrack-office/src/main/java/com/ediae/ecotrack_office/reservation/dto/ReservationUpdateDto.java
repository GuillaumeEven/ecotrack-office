package com.ediae.ecotrack_office.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.users.entity.UserEntity;

public class ReservationUpdateDto {

    // Atributos

    private Long id;
    private LocalDate date;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private Long userId;
    private ResourceEntity resource;

    // Constructores

    public ReservationUpdateDto () {}

    public ReservationUpdateDto (Long id, LocalDate date, ReservationStatus status, LocalDateTime createdAt, Long userId, ResourceEntity resource) {
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
        this.resource = resource;
        this.id = id;
    }

    // Getter y Setter

    public Long getId() {

        return this.id;
    }
    public void setId (Long id) {

        this.id = id;
    }

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

    public Long getUserId () {

        return this.userId;
    }
    public void setUser (Long userId) {

        this.userId = userId;
    }

    public ResourceEntity getResource () {

        return this.resource;
    }
    public void setResource (ResourceEntity resource) {

        this.resource = resource;
    }
}
