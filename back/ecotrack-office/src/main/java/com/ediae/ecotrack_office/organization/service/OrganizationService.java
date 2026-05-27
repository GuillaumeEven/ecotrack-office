package com.ediae.ecotrack_office.organization.service;

import java.util.ArrayList;
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
        System.out.println("La dirección en el modelo es: "+model.getAddress());
        OrganizationEntity entity = OrganizationMapper.toEntity(model);
        entity.setIsActive(true);
        System.out.println("La dirección en la entidad es: "+entity.getAddress());
        repository.save(entity);
        return OrganizationMapper.fromEntity(entity);

    }

    // AQUÍ HE ENTRADO EN CRISIS PORQUE CLARO, SI YO VOY A TENER TODOS LOS DATOS CARGADOS EN LOS MODELOS
    // DEL FRONT PUES LO PUEDO HACER ASÍ DIRECTAMENTE Y A VOLAR, PERO ENTONCES PIERDEN SENTIDO LOS MODELOS 
    // Y LOS DTO (Y HASTA LAS ENTIDADES DIRÍA YO), EN PLAN, PRODRÍA TENER UN ÚNICO OBJETO CON EL QUE TRABAJAR.
    // PERO CLARO, SI NO GUARDO TODOS LOS DATOS EN EL MODELO DEL FRONT, CREO QUE EL ÚNICO QUE NO GUARDARÍA SERÍA
    // LA FECHA DE CREACIÓN PORQUE EN VERDAD TODOS LOS DEMAS TIENEN SENTIDO QUE SEAN EDITABLES, Y BUENO EL ID 
    // NO ES EDITABLE PERO LO QUIERO PARA TRABAJAR CON ÉL, ENTONCES NO SE, O SOLO QUITO LA FECHA DE CREACIÓN O
    // LO DEJO ASÍ XD
    public OrganizationModel updateOrganizationById (Long id, OrganizationUpdateDto dto) {

        Optional <OrganizationEntity> initialEntity = repository.findById(id);
        if(initialEntity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una organización con id: " + id);
        }
        OrganizationEntity entity = initialEntity.get();
        if((repository.findByCif(dto.getCif()).isPresent() && !(dto.getCif().equals(entity.getCif()))) || (repository.findByEmail(dto.getEmail()).isPresent() && !(dto.getEmail().equals(entity.getEmail())))) {

            throw new RuntimeException("Ya existe una organización con este CIF o email.");
        }
        OrganizationModel model = OrganizationMapper.fromUpdateDto(dto);
        OrganizationEntity savedEntity = repository.save(OrganizationMapper.toEntity(model));
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

    public void deleteOrganization (Long id) {

        Optional <OrganizationEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {

            throw new RuntimeException("No se ha encontrado una organización con id: " + id);
        }
        repository.deleteById(id);
    }

}
