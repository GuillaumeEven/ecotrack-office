package com.ediae.ecotrack_office.organization.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class OrganizationCreateDto {

    // Atributos
    
    private String name;
    private String cif;
    private String address;
    private String email;

    // Constructores

    public OrganizationCreateDto () {}

    public OrganizationCreateDto (String name, String cif, String address, String email, Date endSubscription, Boolean isActivate, LocalDateTime createAt) {
        this.name = name;
        this.cif = cif;
        this.address = address;
        this.email = email;
    } 

    // Getters y Setters 

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
}
