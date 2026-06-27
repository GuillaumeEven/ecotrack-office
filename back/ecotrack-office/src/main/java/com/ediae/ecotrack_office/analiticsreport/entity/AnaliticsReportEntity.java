package com.ediae.ecotrack_office.analiticsreport.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "analitics_report")
public class AnaliticsReportEntity {

    // Atributos

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "co_2_savings_kg", nullable = false)
    private Double co2SavingsKg;

    @Column(name = "energy_savings_euros", nullable = false)
    private Double energySavingsEuros;

    @Column(name = "total_reservations", nullable = false)
    private Integer totalReservations;

    @Column(name = "confirmed_check_ins", nullable = false)
    private Integer confirmedCheckIns;

    @Column(name = "empty_rooms", nullable = false)
    private Integer emptyRooms;

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization ;

    // Constructores

    public AnaliticsReportEntity() {}

    public AnaliticsReportEntity(Double co2SavingsKg, Double energySavingsEuros, Integer totalReservations, Integer confirmedCheckIns, Integer emptyRooms, LocalDateTime generatedAt, OrganizationEntity organization) {
        this.co2SavingsKg = co2SavingsKg;
        this.energySavingsEuros = energySavingsEuros;
        this.totalReservations = totalReservations;
        this.confirmedCheckIns = confirmedCheckIns;
        this.emptyRooms = emptyRooms;
        this.generatedAt = LocalDateTime.now(); // Se asigna la fecha y hora actual al crear la entidad
        this.organization = organization;
    }

    //Getter y Setter

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Double getCo2SavingsKg() {
        return this.co2SavingsKg;
    }
    public void setCo2SavingsKg(Double co2SavingsKg) {
        this.co2SavingsKg = co2SavingsKg;
    }

    public Double getEnergySavingsEuros() {
        return this.energySavingsEuros;
    }
    public void setEnergySavingsEuros(Double energySavingsEuros) {
        this.energySavingsEuros = energySavingsEuros;
    }

    public Integer getTotalReservations() {
        return this.totalReservations;
    }
    public void setTotalReservations(Integer totalReservations) {
        this.totalReservations = totalReservations;
    }

    public Integer getConfirmedCheckIns() {
        return this.confirmedCheckIns;
    }
    public void setConfirmedCheckIns(Integer confirmedCheckIns) {
        this.confirmedCheckIns = confirmedCheckIns;
    }

    public Integer getEmptyRooms() {
        return this.emptyRooms;
    }
    public void setEmptyRooms(Integer emptyRooms) {
        this.emptyRooms = emptyRooms;
    }

    public LocalDateTime getGeneratedAt() {
        return this.generatedAt;
    }
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public OrganizationEntity getOrganization() {
        return this.organization;
    }
    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }
}
