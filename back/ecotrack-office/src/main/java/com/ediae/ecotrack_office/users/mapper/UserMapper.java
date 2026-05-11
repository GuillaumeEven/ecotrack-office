package com.ediae.ecotrack_office.users.mapper;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserModel toModel(UserEntity entity) {
        return UserModel.fromEntity(entity);
    }

    public UserEntity toEntity(UserModel model, OrganizationEntity organization) {
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
}
