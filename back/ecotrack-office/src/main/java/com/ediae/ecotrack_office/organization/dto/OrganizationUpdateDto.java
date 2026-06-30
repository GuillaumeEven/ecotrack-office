package com.ediae.ecotrack_office.organization.dto;

public class OrganizationUpdateDto {

    // Atributos
    
    private String name;
    private String cif;
    private String address;
    private String email;

    // Constructores

    public OrganizationUpdateDto () {}

    public OrganizationUpdateDto (String name, String cif, String address, String email) {
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
