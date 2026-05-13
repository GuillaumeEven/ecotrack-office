package com.ediae.ecotrack_office.users.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.users.dto.UserRequestDto;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.mapper.UserMapper;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrganizationRepository organizationRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.organizationRepository = organizationRepository;
    }

    public List<UserModel> getUsersByOrganization(Long organizationId) {
        List<UserEntity> entities = userRepository.findByOrganizationId(organizationId);
        List<UserModel> models = new ArrayList<>();
        for (UserEntity entity : entities) {
            models.add(UserModel.fromEntity(entity));
        }
        return models;
    }

    public UserModel getUserById(Long id) {
        Optional<UserEntity> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        return UserModel.fromEntity(result.get());
    }

    public UserModel createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.email());
        }

        OrganizationEntity organization = organizationRepository.findById(dto.organizationId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con id: " + dto.organizationId()));

        UserModel model = new UserModel();
        model.setEmail(dto.email());
        model.setFirstName(dto.firstName());
        model.setLastName(dto.lastName());
        model.setRole(dto.role());
        model.setOrganizationId(dto.organizationId());
        model.setConsentGiven(dto.consentGiven());
        model.setPreferencesJson(dto.preferencesJson());

        UserEntity entity = userMapper.toEntity(model, organization);
        entity.setPasswordHash(dto.password());
        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(entity);
        return UserModel.fromEntity(saved);
    }

    public UserModel updateUser(Long id, UserRequestDto dto) {
        Optional<UserEntity> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }

        UserEntity entity = result.get();
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setRole(dto.role());
        entity.setConsentGiven(dto.consentGiven());
        entity.setPreferencesJson(dto.preferencesJson());

        UserEntity saved = userRepository.save(entity);
        return UserModel.fromEntity(saved);
    }

    public void deactivateUser(Long id) {
        Optional<UserEntity> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        UserEntity entity = result.get();
        entity.setIsActive(false);
        userRepository.save(entity);
    }
}