package com.ediae.ecotrack_office.audit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "anl_audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qué ocurrió: "ROLE_CHANGED", "USER_DEACTIVATED", "USER_DELETED"...
    @Column(name = "event_type", nullable = false)
    private String eventType;

    // Sobre qué tipo de objeto: siempre "USER" en nuestro caso
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    // El id del objeto afectado: el id del usuario que fue modificado
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    // El id del usuario que hizo la acción: el ADMIN que realizó el cambio
    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    // Cuándo ocurrió
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}