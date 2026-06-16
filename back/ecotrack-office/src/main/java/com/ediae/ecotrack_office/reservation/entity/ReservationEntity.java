package com.ediae.ecotrack_office.reservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
import com.ediae.ecotrack_office.users.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservation")
public class ReservationEntity {

    // Atributos

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private LocalDate date;

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private ReservationStatus status;

    @CreationTimestamp
    @Column (name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne (optional = false)
    @JoinColumn (name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne (optional = false)
    @JoinColumn (name = "resource_id", nullable = false)
    private ResourceEntity resource;

    // Constructores

    public ReservationEntity () {}

    public ReservationEntity (LocalDate date, ReservationStatus status, UserEntity user, ResourceEntity resource) {
        this.date = date;
        this.status = status;
        this.user = user;
        this.resource = resource;
        // createdAt is automatically set by @CreationTimestamp on insert
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
