package com.ediae.ecotrack_office.incident.model;

import java.time.LocalDateTime;

import com.ediae.ecotrack_office.incident.enums.IncidentStatus;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder.In;

@Entity
@Table (name = "incidents")
public class IncidentEntity {

    // Atributos

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String description;

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private IncidentStatus status;

    @Column (name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column (name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne (optional = false)
    @JoinColumn (name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne (optional = false)
    @JoinColumn (name = "resource_id", nullable = false)
    private Long resourceId;

    // Constructores

    public IncidentEntity () {}

    public IncidentEntity (String description, IncidentStatus status, LocalDateTime createdAt, Long userId, Long resourceId) {
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = null;
        this.userId = userId;
        this.resourceId = resourceId;
    }

    // Getter y Setter

    public Long getId () {

        return this.id;
    }
    public void setId (Long id) {
        this.id = id;
    }

    public String getDescription () {

        return this.description;
    }
    public void setDescription (String description) {
        this.description = description;
    }

    public IncidentStatus getStatus () {

        return this.status;
    }
    public void setStatus (IncidentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt () {

        return this.createdAt;
    }
    public void setCreatedAt (LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt () {

        return this.resolvedAt;
    }
    public void setResolvedAt (LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
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
