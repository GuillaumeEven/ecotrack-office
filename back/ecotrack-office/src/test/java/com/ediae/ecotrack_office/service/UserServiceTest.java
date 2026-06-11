package com.ediae.ecotrack_office.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ediae.ecotrack_office.audit.service.AuditLogService;
import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import com.ediae.ecotrack_office.shared.exception.NotFoundException;
import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.users.mapper.UserMapper;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.repository.UserRepository;
import com.ediae.ecotrack_office.users.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AuditLogService auditLogService; // ← AÑADIDO: faltaba este mock

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        OrganizationEntity org = new OrganizationEntity();
        org.setId(1L);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("ana@empresa.com");
        userEntity.setFirstName("Ana");
        userEntity.setLastName("García");
        userEntity.setRole(Role.EMPLOYEE);
        userEntity.setOrganization(org);
        userEntity.setIsActive(true);
        userEntity.setConsentGiven(true);
        userEntity.setCreatedAt(LocalDateTime.now());
    }

    // ─────────────────────────────────────────────
    // getUserById
    // ─────────────────────────────────────────────

    @Test
    void getUserById_usuarioExiste_devuelveModel() {
        // Decimos qué devuelve el repositorio
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        // Decimos qué devuelve el mapper cuando recibe la entidad
        // NECESARIO porque userMapper es un mock y por defecto devuelve null
        when(userMapper.toModel(userEntity)).thenReturn(UserModel.fromEntity(userEntity));

        UserModel result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("ana@empresa.com", result.getEmail());
        assertEquals("Ana", result.getFirstName());
    }

    @Test
    void getUserById_usuarioNoExiste_lanzaNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Ahora lanza NotFoundException en lugar de RuntimeException
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.getUserById(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─────────────────────────────────────────────
    // updateMe
    // ─────────────────────────────────────────────

    @Test
    void updateMe_datosValidos_actualizaYDevuelveModel() {
        // CORREGIDO: UserMeRequestDto ahora tiene 4 campos (añadido consentGiven)
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", "analopez@example.com", null,  null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        // AÑADIDO: mockeamos toModel porque el mapper es un mock
        when(userMapper.toModel(any(UserEntity.class))).thenReturn(UserModel.fromEntity(userEntity));

        UserModel result = userService.updateMe(1L, dto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void updateMe_usuarioNoExiste_lanzaNotFoundException() {
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", "analopez@example.com", null,  null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.updateMe(99L, dto));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void updateMe_intentaModificarEmail_emailNoCambia() {
        // El DTO de updateMe no tiene campo email, así que nunca puede cambiarlo
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", "analopez@example.com", null,  null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toModel(any(UserEntity.class))).thenReturn(UserModel.fromEntity(userEntity));

        UserModel result = userService.updateMe(1L, dto);

        // El email sigue siendo el original
        assertEquals("ana@empresa.com", result.getEmail());
    }

    @Test
    void updateMe_conConsentimientoFalse_actualizaConsentimiento() {
        // Verificamos que el usuario puede retirar su consentimiento GDPR
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", "analopez@example.com", false, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toModel(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity saved = inv.getArgument(0);
            return UserModel.fromEntity(saved);
        });

        UserModel result = userService.updateMe(1L, dto);

        assertFalse(result.getConsentGiven());
    }

    @Test
    void updateMe_conPreferencias_guardaLasPreferencias() {
        String prefs = "{\"theme\":\"dark\"}";
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", "analopez@example.com", null, prefs);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toModel(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity saved = inv.getArgument(0);
            return UserModel.fromEntity(saved);
        });

        UserModel result = userService.updateMe(1L, dto);

        assertEquals(prefs, result.getPreferencesJson());
    }
}