package com.ediae.ecotrack_office.analiticsreport.model;

import java.time.LocalDateTime;

public class AnaliticsReportModel {
    private Long id;
    private Double co2Saved;
    private LocalDateTime createdAt;
    private Long organizationId; // ID plano 

    // Constructor vacío
    public AnaliticsReportModel() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getCo2Saved() { return co2Saved; }
    public void setCo2Saved(Double co2Saved) { this.co2Saved = co2Saved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}