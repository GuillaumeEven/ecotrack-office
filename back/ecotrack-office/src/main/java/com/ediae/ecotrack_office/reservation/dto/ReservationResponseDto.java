package com.ediae.ecotrack_office.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ediae.ecotrack_office.assets.dto.ResourceResponseDto;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;

public class ReservationResponseDto {

     // Atributos

    private Long id;
    private LocalDate date;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private Long userId;
    private String resourceName;
    private String resourceEquipmentList;

    // Constructores

    public ReservationResponseDto () {}

    public ReservationResponseDto (Long id, LocalDate date, ReservationStatus status, LocalDateTime createdAt, Long userId, String resourceName, String resourceEquipmentList) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
        this.resourceName = resourceName;
        this.resourceEquipmentList = resourceEquipmentList;
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
    public void setCreatedAt (LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public Long getUserId () {

        return this.userId;
    }
    public void setUserId (Long userId) {

        this.userId = userId;
    }

    public String getResourceName () {

        return this.resourceName;
    }
    public void setResourceName (String resourceName) {

        this.resourceName = resourceName;
    }

    public String getResourceEquipmentList () {

        return this.resourceEquipmentList;
    }
    public void setResourceEquipmentList (String resourceEquipmentList) {

        this.resourceEquipmentList = resourceEquipmentList;
    }
}
