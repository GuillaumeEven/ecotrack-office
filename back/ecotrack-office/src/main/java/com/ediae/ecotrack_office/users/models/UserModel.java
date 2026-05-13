package com.ediae.ecotrack_office.users.models;

import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;
import java.time.LocalDateTime;

public class UserModel {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Long organizationId;
    private Boolean isActive;
    private Boolean consentGiven;
    private String preferencesJson;
    private LocalDateTime createdAt;

    public UserModel() {}

    public static UserModel fromEntity(UserEntity entity) {
        UserModel model = new UserModel();
        model.setId(entity.getId());
        model.setEmail(entity.getEmail());
        model.setFirstName(entity.getFirstName());
        model.setLastName(entity.getLastName());
        model.setRole(entity.getRole());
        model.setOrganizationId(entity.getOrganization().getId());
        model.setIsActive(entity.getIsActive());
        model.setConsentGiven(entity.getConsentGiven());
        model.setPreferencesJson(entity.getPreferencesJson());
        model.setCreatedAt(entity.getCreatedAt());
        return model;
    }

    public UserResponseDto toResponseDto() {
        return new UserResponseDto(
            this.id,
            this.email,
            this.firstName,
            this.lastName,
            this.role,
            this.organizationId,
            this.isActive,
            this.consentGiven,
            this.preferencesJson,
            this.createdAt
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getConsentGiven() { return consentGiven; }
    public void setConsentGiven(Boolean consentGiven) { this.consentGiven = consentGiven; }

    public String getPreferencesJson() { return preferencesJson; }
    public void setPreferencesJson(String preferencesJson) { this.preferencesJson = preferencesJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}