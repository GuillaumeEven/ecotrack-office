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

    // ─────────────────────────────────────────────
    // GET — lista paginada de usuarios por organización
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public PageResponseDto<UserResponseDto> getUsersByOrganization(Long organizationId, Pageable pageable) {
        Page<UserEntity> page = userRepository.findByOrganizationId(organizationId, pageable);

        List<UserResponseDto> content = page.getContent().stream()
                .map(entity -> userMapper.toModel(entity).toResponseDto())
                .toList();

        return new PageResponseDto<>(page, content);
    }

    // ─────────────────────────────────────────────
    // GET — un usuario por id
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public UserModel getUserById(Long id) {
        return userMapper.toModel(
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id))
        );
    }

    // ─────────────────────────────────────────────
    // POST — crear usuario
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public UserModel createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.email());
        }

        OrganizationEntity organization = organizationRepository.findById(dto.organizationId())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada con id: " + dto.organizationId()));

        UserEntity entity = userMapper.toEntityFromDto(dto, organization);
        
        // 🆕 HASHEAR CONTRASEÑA AQUÍ
        entity.setPasswordHash(passwordEncoder.encode(dto.password()));
        
        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setConsentGiven(false);

        UserEntity saved = userRepository.save(entity);
        auditLogService.log("USER_CREATED", "USER", saved.getId(), RequestContext.getUserId());

        return userMapper.toModel(saved);
    }

    // ─────────────────────────────────────────────
    // POST — crear usuario
    // Público para poder hacer el registro
    // ─────────────────────────────────────────────
    public UserModel createUserWithCif(UserCreateRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.email());
        }

        OrganizationEntity organization = organizationRepository.findByCif(dto.cif())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada con CIF: " + dto.cif()));

        UserEntity entity = userMapper.fromCreateDtoEntity(dto, organization);
        
        // 🆕 HASHEAR CONTRASEÑA AQUÍ
        entity.setPasswordHash(passwordEncoder.encode(dto.password()));
        
        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(entity);
        return userMapper.toModel(saved);
    }

    // ─────────────────────────────────────────────
    // PUT — el ADMIN modifica cualquier usuario
    // Si cambia el rol, se registra en el audit log
    // ─────────────────────────────────────────────
    public UserModel updateUser(Long id, UserRequestDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        // 🆕 Preservamos los valores no mutables o requeridos que Angular no maneja en el form común
        var previousRole = entity.getRole();
        var currentConsent = entity.getConsentGiven();
        var createdAt = entity.getCreatedAt();

        // El mapper actualiza los campos comunes
        userMapper.updateEntityFromDto(dto, entity);

        // El rol solo lo gestiona el ADMIN desde este método
        entity.setRole(dto.role());

        // 🆕 Forzamos a mantener los valores previos si el mapper los ha machacado con null
        if (entity.getConsentGiven() == null) {
            entity.setConsentGiven(currentConsent != null ? currentConsent : false);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(createdAt);
        }

        UserEntity saved = userRepository.save(entity);

        // Si el rol cambió, lo registramos en el audit log
        if (!previousRole.equals(dto.role())) {
            auditLogService.log(
                "ROLE_CHANGED",   // qué pasó
                "USER",           // sobre qué tipo de objeto
                id,               // id del usuario afectado
                RequestContext.getUserId() // id del admin que lo hizo
            );
        }

        return userMapper.toModel(saved);
    }

    // ─────────────────────────────────────────────
    // PATCH — desactivar usuario
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public void deactivateUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        entity.setIsActive(false);
        userRepository.save(entity);

        auditLogService.log("USER_DEACTIVATED", "USER", id, RequestContext.getUserId());
    }

    // ─────────────────────────────────────────────
    // DELETE — GDPR erasure
    // Solo ADMIN (se verifica en el Controller)
    // No borra la fila, anonimiza los datos personales
    // ─────────────────────────────────────────────
    public void deleteUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        // Anonimizamos todos los datos personales
        entity.setEmail("deleted_" + id + "@deleted.local");
        entity.setFirstName("DELETED");
        entity.setLastName("DELETED");
        entity.setPasswordHash("");
        entity.setConsentGiven(false);
        entity.setPreferencesJson(null);
        entity.setIsActive(false);

        userRepository.save(entity);

        auditLogService.log("USER_DELETED", "USER", id, RequestContext.getUserId());
    }

    // ─────────────────────────────────────────────
    // PATCH — el usuario edita sus propios datos
    // Cualquier usuario autenticado
    // ─────────────────────────────────────────────
    public UserModel updateMe(Long id, UserMeRequestDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));
        if(userRepository.findByEmail(dto.email()).isPresent()){

            throw new IllegalArgumentException("Este email ya está en uso.");
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

    // ─────────────────────────────────────────────
    // PATCH — el usuario cambia su propia contraseña
    // Cualquier usuario autenticado
    // ─────────────────────────────────────────────
    public void changePassword(Long id, ChangePasswordRequestDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        // 🆕 Cambiado de .equals() a passwordEncoder.matches()
        if (!passwordEncoder.matches(dto.currentPassword(), entity.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }

        // 🆕 Cambiado a passwordEncoder.encode() para guardar el nuevo hash seguro
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

    // ─────────────────────────────────────────────
    // PATCH — reactivar usuario
    // Solo ADMIN (se verifica en el Controller)
    // ─────────────────────────────────────────────
    public void reactivateUser(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));

        entity.setIsActive(true);
        userRepository.save(entity);

        auditLogService.log("USER_REACTIVATED", "USER", id, RequestContext.getUserId());
    }
}