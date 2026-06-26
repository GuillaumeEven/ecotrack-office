package com.ediae.ecotrack_office.organization.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.organization.dto.OrganizationCreateDto;
import com.ediae.ecotrack_office.organization.dto.OrganizationUpdateDto;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.mapper.OrganizationMapper;
import com.ediae.ecotrack_office.organization.model.OrganizationModel;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository repository;

    public List <OrganizationModel> getAllOrganizations () {
        
        List <OrganizationEntity> entities = repository.findAll();
        List <OrganizationModel> models = new ArrayList <>();
        for (OrganizationEntity entity : entities) {

            models.add(OrganizationMapper.fromEntity(entity));
        }
        return models;
    }

    public OrganizationModel getOrganizationById (Long id) {

        Optional <OrganizationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una organización con id: " + id);
        }
        return OrganizationMapper.fromEntity(entity.get());
    }

    public OrganizationModel createOrganization (OrganizationCreateDto dto) {

        if (repository.findByCif(dto.getCif()).isPresent() || repository.findByEmail(dto.getEmail()).isPresent()) {

            throw new RuntimeException ("Ya existe una organización con este CIF o email.");
        }
        OrganizationModel model = OrganizationMapper.fromCreateDto(dto);
        OrganizationEntity entity = OrganizationMapper.toEntity(model);
        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        LocalDateTime endSubs = LocalDateTime.now().plusDays(30);
        Date endSubsConvert = Date.from(endSubs.atZone(ZoneId.systemDefault()).toInstant());
        entity.setEndSubscription(endSubsConvert);
        repository.save(entity);
        return OrganizationMapper.fromEntity(entity);

    }

    public OrganizationModel updateOrganizationById (Long id, OrganizationUpdateDto dto) {

        Optional <OrganizationEntity> initialEntity = repository.findById(id);
        if(initialEntity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una organización con id: " + id);
        }
        OrganizationEntity entity = initialEntity.get();
        if((repository.findByCif(dto.getCif()).isPresent() && !(dto.getCif().equals(entity.getCif()))) || (repository.findByEmail(dto.getEmail()).isPresent() && !(dto.getEmail().equals(entity.getEmail())))) {

            throw new RuntimeException("Ya existe una organización con este CIF o email.");
        }
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setCif(dto.getCif());
        entity.setEmail(dto.getEmail());
        OrganizationEntity savedEntity = repository.save(entity);
        return OrganizationMapper.fromEntity(savedEntity);
    }

    public OrganizationModel deactivateOrganization (Long id) {

        Optional <OrganizationEntity> initialEntity = repository.findById(id);
        if (initialEntity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una organización con id: " + id);
        }
        OrganizationEntity entity = initialEntity.get();
        entity.setIsActive(false);
        OrganizationEntity savedEntity = repository.save(entity);
        return OrganizationMapper.fromEntity(savedEntity);
    }

    public Boolean deleteOrganization (Long id) {

        Optional <OrganizationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una organización con id: " + id);
        }
        repository.deleteById(id);
        if(repository.findById(id).isEmpty()) return true;
        else return false;
    }

}
