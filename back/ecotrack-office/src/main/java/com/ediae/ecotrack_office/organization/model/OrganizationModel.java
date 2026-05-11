package com.ediae.ecotrack_office.organization.model;

import java.time.LocalDateTime;
import java.util.Date;

import com.ediae.ecotrack_office.organization.dto.OrganizationCreateDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationResponseDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationUpdateDto;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;

public class OrganizationModel {

    // Atributos
    
    private Long id;
    private String name;
    private String CIF;
    private String address;
    private String email;
    private Date endSubscription;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructores

    public OrganizationModel () {}

    public OrganizationModel (Long id, String name, String CIF, String address, String email, Date endSubscription, Boolean isActivate, LocalDateTime createAt) {
        this.id = id;
        this.name = name;
        this.CIF = CIF;
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

    // Médoto para pasar de OrganizationEntity a OrganizationModel

    public static OrganizationModel fromEntity (OrganizationEntity entity) {

        return new OrganizationModel(
            entity.getId(),
            entity.getName(),
            entity.getCIF(),
            entity.getAddress(),
            entity.getEmail(),
            entity.getEndSubscription(),
            entity.getIsActive(),
            entity.getCreatedAt()
        );
    }

    // Método para pasar de OrganizationModel a OrganizationEntity

    public OrganizationEntity toEntity () {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setCIF(this.CIF);
        entity.setAddress(this.address);
        entity.setEmail(this.email);
        entity.setEndSubscription(this.endSubscription);
        entity.setIsActive(this.isActive);
        entity.setCreatedAt(this.createdAt);
        return entity;
    }

    // Método para pasar de OrganizationCreateDto a OrganizationModel

    public static OrganizationModel fromCreateDto (OrganizationCreateDto createDto) {

        return new OrganizationModel (
            null,
            createDto.getName(),
            createDto.getCIF(),
            createDto.getAddress(),
            createDto.getEmail(),
            createDto.getEndSubscription(),
            createDto.getIsActive(),
            createDto.getCreatedAt()
         );
    }

    // Método para pasar de un OrganizationUpdateDto a OrganizationModel

    public static OrganizationModel fromUpdateDto (OrganizationUpdateDto updateDto) {

        return new OrganizationModel (
            updateDto.getId(),
            updateDto.getName(),
            updateDto.getCIF(),
            updateDto.getAddress(),
            updateDto.getEmail(),
            updateDto.getEndSubscription(),
            updateDto.getIsActive(),
            updateDto.getCreatedAt()
         );
    }

    // Método para pasar de OrganizationModel a OrganizationResponseDto

    public OrganizationResponseDto toResponseDto () {

        OrganizationResponseDto responseDto = new OrganizationResponseDto();
        responseDto.setId(this.id);
        responseDto.setName(this.name);
        responseDto.setCIF(this.CIF);
        responseDto.setAddress(this.address);
        responseDto.setEmail(this.email);
        responseDto.setEndSubscription(this.endSubscription);
        responseDto.setIsActive(this.isActive);
        responseDto.setCreatedAt(this.createdAt);
        return responseDto;
    }
}
