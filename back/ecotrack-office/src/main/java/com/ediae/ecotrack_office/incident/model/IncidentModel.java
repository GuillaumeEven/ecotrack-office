package com.ediae.ecotrack_office.incident.model;

import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import java.time.LocalDateTime;

public class IncidentModel {
    private Long id;
    private String description;
    private IncidentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private Long userId;
    private Long resourceId;

    public IncidentModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
}