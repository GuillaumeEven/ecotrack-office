package com.ediae.ecotrack_office.users.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ediae.ecotrack_office.audit.service.AuditLogService;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.shared.context.RequestContext;
import com.ediae.ecotrack_office.shared.dto.PageResponseDto;
import com.ediae.ecotrack_office.shared.exception.ApplicationException;
import com.ediae.ecotrack_office.shared.exception.ErrorCode;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.dto.ChangePasswordRequestDto;
import com.ediae.ecotrack_office.users.dto.UserCreateRequestDto;
import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.dto.UserRequestDto;
import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.dto.UserStatsDto;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.users.mapper.UserMapper;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       OrganizationRepository organizationRepository,
                       AuditLogService auditLogService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.organizationRepository = organizationRepository;
        this.auditLogService = auditLogService;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResponseDto<UserResponseDto> getUsersByOrganization(Long organizationId, Pageable pageable) {
        Page<UserEntity> page = userRepository.findByOrganizationId(organizationId, pageable);

        List<UserResponseDto> content = page.getContent().stream()
                .map(entity -> userMapper.toModel(entity).toResponseDto())
                .toList();

        return new PageResponseDto<>(page, content);
    }

    public UserModel getUserById(Long id) {
        return userMapper.toModel(
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id))
        );
    }

    public UserModel createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ApplicationException(ErrorCode.DUPLICATE_RESOURCE, "Ya existe un usuario con el email: " + dto.email());
        }

        OrganizationEntity organization = organizationRepository.findById(dto.organizationId())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada con id: " + dto.organizationId()));

        UserEntity entity = userMapper.toEntityFromDto(dto, organization);

        entity.setPasswordHash(passwordEncoder.encode(dto.password()));

        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setConsentGiven(false);

        UserEntity saved = userRepository.save(entity);
        auditLogService.log("USER_CREATED", "USER", saved.getId(), RequestContext.getUserId());

        return userMapper.toModel(saved);
    }

    public UserModel createUserWithCif(UserCreateRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ApplicationException(ErrorCode.DUPLICATE_RESOURCE, "Ya existe un usuario con el email: " + dto.email());
        }

        OrganizationEntity organization = organizationRepository.findByCif(dto.cif())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada con CIF: " + dto.cif()));

        UserEntity entity = userMapper.fromCreateDtoEntity(dto, organization);

        entity.setPasswordHash(passwordEncoder.encode(dto.password()));

        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(entity);
        return userMapper.toModel(saved);
    }

    public UserModel updateUser(Long id, UserRequestDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        var previousRole = entity.getRole();
        var currentConsent = entity.getConsentGiven();
        var createdAt = entity.getCreatedAt();

        userMapper.updateEntityFromDto(dto, entity);

        entity.setRole(dto.role());

        if (entity.getConsentGiven() == null) {
            entity.setConsentGiven(currentConsent != null ? currentConsent : false);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(createdAt);
        }

        UserEntity saved = userRepository.save(entity);

        if (!previousRole.equals(dto.role())) {
            auditLogService.log(
                "ROLE_CHANGED",
                "USER",
                id,
                RequestContext.getUserId()
            );
        }

        return userMapper.toModel(saved);
    }

    public void deactivateUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        entity.setIsActive(false);
        userRepository.save(entity);

        auditLogService.log("USER_DEACTIVATED", "USER", id, RequestContext.getUserId());
    }

    public void deleteUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        if (entity.getRole() == Role.ADMIN) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "No se puede eliminar un usuario con rol ADMIN");
        }

        userRepository.deleteById(id);

        auditLogService.log("USER_DELETED", "USER", id, RequestContext.getUserId());
    }

    public UserModel updateMe(Long id, UserMeRequestDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));
        if(userRepository.findByEmail(dto.email()).isPresent()){

            throw new ApplicationException(ErrorCode.DUPLICATE_RESOURCE, "Este email ya está en uso.");
        }

        entity.setFirstName(dto.firstName().trim());
        entity.setLastName(dto.lastName().trim());
        entity.setEmail(dto.email().trim());

        if (dto.consentGiven() != null) {
            entity.setConsentGiven(dto.consentGiven());
        }

        if (dto.preferencesJson() != null) {
            entity.setPreferencesJson(dto.preferencesJson());
        }

        return userMapper.toModel(userRepository.save(entity));
    }

    public void changePassword(Long id, ChangePasswordRequestDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        if (!passwordEncoder.matches(dto.currentPassword(), entity.getPasswordHash())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT, "La contraseña actual no es correcta");
        }

        entity.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(entity);

        auditLogService.log("PASSWORD_CHANGED", "USER", id, id);
    }

    public Long getOrganizationIdByUserId(Long userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + userId));
        return entity.getOrganization().getId();
    }

    // ─────────────────────────────────────────────
    // GET — lista paginada con filtros opcionales
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public PageResponseDto<UserResponseDto> getUsersByFilters(
            Long organizationId,
            String search,
            Role role,
            Boolean isActive,
            Pageable pageable) {

        // Si search está vacío lo tratamos como null para que el filtro lo ignore
        String searchParam = (search != null && !search.isBlank()) ? search : null;

        Page<UserEntity> page = userRepository.findByFilters(
            organizationId, searchParam, role, isActive, pageable
        );

        List<UserResponseDto> content = page.getContent().stream()
                .map(entity -> userMapper.toModel(entity).toResponseDto())
                .toList();

        return new PageResponseDto<>(page, content);
    }

    // ─────────────────────────────────────────────
    // GET — estadísticas para las KPI cards
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public UserStatsDto getUserStats(Long organizationId) {
        long total    = userRepository.countByOrganizationId(organizationId);
        long active   = userRepository.countByOrganizationIdAndIsActive(organizationId, true);
        long newThisMonth = userRepository.countNewUsersThisMonth(
            organizationId,
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        );
        return new UserStatsDto(total, active, newThisMonth);
    }

    public void reactivateUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        entity.setIsActive(true);
        userRepository.save(entity);

        auditLogService.log("USER_REACTIVATED", "USER", id, RequestContext.getUserId());
    }
}