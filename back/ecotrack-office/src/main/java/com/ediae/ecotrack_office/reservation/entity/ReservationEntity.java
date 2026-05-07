package com.ediae.ecotrack_office.reservation.entity;

import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.assets.entity.ResourceEntity;
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
    private UserEntity user;

    @ManyToOne (optional = false)
    @JoinColumn (name = "resource_id", nullable = false)
    private ResourceEntity resource;

    // Constructores

    public ReservationEntity () {}

    public ReservationEntity (LocalDate date, ReservationStatus status, LocalDateTime createdAt, UserEntity user, ResourceEntity resource) {
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
        this.resource = resource;
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
