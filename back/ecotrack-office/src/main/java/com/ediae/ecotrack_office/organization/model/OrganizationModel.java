package com.ediae.ecotrack_office.organization.model;

import java.time.LocalDateTime;
import java.util.Date;

public class OrganizationModel {

    // Atributos
    
    private Long id;
    private String name;
    private String cif;
    private String address;
    private String email;
    private Date endSubscription;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructores

    public OrganizationModel () {}

    public OrganizationModel (Long id, String name, String cif, String address, String email, Date endSubscription, Boolean isActivate, LocalDateTime createAt) {
        this.id = id;
        this.name = name;
        this.cif = cif;
        this.address = address;
        this.email = email;
        this.endSubscription = endSubscription;
        this.isActive = isActivate;
        this.createdAt = createAt;
    } 

    // Getters y Setters 

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCif() {
        return cif;
    }
    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getEndSubscription() {
        return endSubscription;
    }
    public void setEndSubscription(Date endSubscription) {
        this.endSubscription = endSubscription;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
