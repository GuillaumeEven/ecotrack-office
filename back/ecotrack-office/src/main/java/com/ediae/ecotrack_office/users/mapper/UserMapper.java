package com.ediae.ecotrack_office.users.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.users.dto.UserRequestDto;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.models.UserModel;

@Component
public class UserMapper {

    public UserModel toModel(UserEntity entity) {
        if (entity == null) return null;
        return UserModel.fromEntity(entity);
    }

    public UserEntity toEntity(UserModel model, OrganizationEntity organization) {
        if (model == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setEmail(model.getEmail());
        entity.setFirstName(model.getFirstName());
        entity.setLastName(model.getLastName());
        entity.setRole(model.getRole());
        entity.setIsActive(model.getIsActive());
        entity.setConsentGiven(model.getConsentGiven());
        entity.setPreferencesJson(model.getPreferencesJson());
        entity.setOrganization(organization);
        return entity;
    }

    /**
     * Crea una entidad nueva directamente desde el DTO de creación.
     * Evita construir un UserModel intermedio innecesario en createUser().
     */
    public UserEntity toEntityFromDto(UserRequestDto dto, OrganizationEntity organization) {
        if (dto == null) return null;

        UserEntity entity = new UserEntity();
        entity.setEmail(dto.email());
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setRole(dto.role());
        entity.setConsentGiven(dto.consentGiven());
        entity.setPreferencesJson(dto.preferencesJson());
        entity.setOrganization(organization);
        return entity;
    }

    /**
     * Actualiza los campos modificables de una entidad existente desde el DTO.
     * El rol NO se mapea aquí por seguridad: se controla en el Service según permisos.
     */
    public void updateEntityFromDto(UserRequestDto dto, UserEntity entity) {
        if (dto == null || entity == null) return;

        entity.setEmail(dto.email());
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setConsentGiven(dto.consentGiven());
        entity.setPreferencesJson(dto.preferencesJson());
    }
}