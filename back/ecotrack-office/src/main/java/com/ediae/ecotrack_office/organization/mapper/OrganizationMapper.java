package com.ediae.ecotrack_office.organization.mapper;

import org.springframework.stereotype.Component;

import com.ediae.ecotrack_office.organization.dto.OrganizationCreateDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationResponseDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationUpdateDto;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.model.OrganizationModel;

@Component
public class OrganizationMapper {

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

    public static OrganizationEntity toEntity (OrganizationModel model) {
        
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setCIF(model.getCIF());
        entity.setAddress(model.getAddress());
        entity.setEmail(model.getEmail());
        entity.setEndSubscription(model.getEndSubscription());
        entity.setIsActive(model.getIsActive());
        entity.setCreatedAt(model.getCreatedAt());
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

    public static OrganizationResponseDto toResponseDto (OrganizationModel model) {

        OrganizationResponseDto responseDto = new OrganizationResponseDto();
        responseDto.setId(model.getId());
        responseDto.setName(model.getName());
        responseDto.setCIF(model.getCIF());
        responseDto.setAddress(model.getAddress());
        responseDto.setEmail(model.getEmail());
        responseDto.setEndSubscription(model.getEndSubscription());
        responseDto.setIsActive(model.getIsActive());
        responseDto.setCreatedAt(model.getCreatedAt());
        return responseDto;
    }

}
