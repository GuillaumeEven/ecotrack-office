package com.ediae.ecotrack_office.analyticsreport.model;

import java.time.LocalDateTime;

public class AnalyticsReportModel {
    private Long id;
    private Double co2Saved;
    private LocalDateTime createdAt;
    private Long organizationId;

    // Campos añadidos para completar el modelo con las métricas de analítica para el frontend
    private Double co2SavingsKg;
    private Double energySavingsEuros;
    private Integer totalReservations;
    private Integer confirmedCheckIns;
    private Integer emptyRooms;

    public AnalyticsReportModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getCo2Saved() { return co2Saved; }
    public void setCo2Saved(Double co2Saved) { this.co2Saved = co2Saved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Double getCo2SavingsKg() { return co2SavingsKg; }
    public void setCo2SavingsKg(Double co2SavingsKg) { this.co2SavingsKg = co2SavingsKg; }

    public Double getEnergySavingsEuros() { return energySavingsEuros; }
    public void setEnergySavingsEuros(Double energySavingsEuros) { this.energySavingsEuros = energySavingsEuros; }

    public Integer getTotalReservations() { return totalReservations; }
    public void setTotalReservations(Integer totalReservations) { this.totalReservations = totalReservations; }

    public Integer getConfirmedCheckIns() { return confirmedCheckIns; }
    public void setConfirmedCheckIns(Integer confirmedCheckIns) { this.confirmedCheckIns = confirmedCheckIns; }

    public Integer getEmptyRooms() { return emptyRooms; }
    public void setEmptyRooms(Integer emptyRooms) { this.emptyRooms = emptyRooms; }
}