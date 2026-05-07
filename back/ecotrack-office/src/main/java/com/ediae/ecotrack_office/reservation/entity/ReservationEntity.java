package com.ediae.ecotrack_office.reservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

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

    @Column (name = "created_at", nullable = false) //TODO: ¿Debería de poner aqui un temporaltype.timestamp?
    private LocalDateTime createdAt;

    @ManyToOne (optional = false)
    @JoinColumn (name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne (optional = false)
    @JoinColumn (name = "resource_id", nullable = false)
    private Long resourceId;

    // Constructores

    public ReservationEntity () {}

    public ReservationEntity (LocalDate date, ReservationStatus status, LocalDateTime createdAt, Long userId, Long resourceId) {
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
        this.resourceId = resourceId;
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
