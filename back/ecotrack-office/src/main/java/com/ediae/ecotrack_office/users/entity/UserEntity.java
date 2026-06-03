package com.ediae.ecotrack_office.users.entity;

import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.ediae.ecotrack_office.incident.model.IncidentEntity;
import com.ediae.ecotrack_office.reservation.entity.ReservationEntity;


@Entity
@Table(name = "usr_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne (optional = false)
    @JoinColumn (name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven;

    private String preferencesJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany (mappedBy = "user")
    private List<IncidentEntity> incidences;

    @OneToMany (mappedBy = "user")
    private List<ReservationEntity> reservations;



    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public OrganizationEntity getOrganization() { return organization; }
    public void setOrganization(OrganizationEntity organization) { this.organization = organization; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getConsentGiven() { return consentGiven; }
    public void setConsentGiven(Boolean consentGiven) { this.consentGiven = consentGiven; }

    public String getPreferencesJson() { return preferencesJson; }
    public void setPreferencesJson(String preferencesJson) { this.preferencesJson = preferencesJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
