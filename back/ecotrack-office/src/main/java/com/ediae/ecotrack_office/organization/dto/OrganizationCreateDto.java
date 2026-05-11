package com.ediae.ecotrack_office.organization.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class OrganizationCreateDto {

    // Atributos
    
    private String name;
    private String CIF;
    private String address;
    private String email;
    private Date endSubscription;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructores

    public OrganizationCreateDto () {}

    public OrganizationCreateDto (String name, String CIF, String address, String email, Date endSubscription, Boolean isActivate, LocalDateTime createAt) {
        this.name = name;
        this.CIF = CIF;
        this.address = address;
        this.email = email;
        this.endSubscription = endSubscription;
        this.isActive = isActivate;
        this.createdAt = createAt;
    } 

    // Getters y Setters 

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCIF() {
        return CIF;
    }
    public void setCIF(String CIF) {
        this.CIF = CIF;
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
