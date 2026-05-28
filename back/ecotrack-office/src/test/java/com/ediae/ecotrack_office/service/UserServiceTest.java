package com.ediae.ecotrack_office.service;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;
import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;
import com.ediae.ecotrack_office.users.mapper.UserMapper;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.repository.UserRepository;
import com.ediae.ecotrack_office.organization.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.HtmlUtils;
import com.ediae.ecotrack_office.users.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrganizationRepository organizationRepository;

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

    @Test
    void getMe_usuarioExiste_devuelveModel() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserModel result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("ana@empresa.com", result.getEmail());
        assertEquals("Ana", result.getFirstName());
    }

    @Test
    void getMe_usuarioNoExiste_lanzaExcepcion() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserById(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void updateMe_datosValidos_actualizaYDevuelveModel() {
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel result = userService.updateMe(1L, dto);

        assertNotNull(result);
        assertEquals("Ana", result.getFirstName());
        assertEquals("López", result.getLastName());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void updateMe_usuarioNoExiste_lanzaExcepcion() {
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateMe(99L, dto));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void updateMe_nombreVacio_lanzaExcepcion() {
        UserMeRequestDto dto = new UserMeRequestDto("", "López", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateMe(1L, dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateMe_apellidoVacio_lanzaExcepcion() {
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateMe(1L, dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateMe_intentaModificarEmail_ignoraElCampo() {
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel result = userService.updateMe(1L, dto);

        assertEquals("ana@empresa.com", result.getEmail());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void updateMe_conPreferencias_sanitizaElTexto() {
        UserMeRequestDto dto = new UserMeRequestDto("Ana", "López", "<script>alert('xss')</script>");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel result = userService.updateMe(1L, dto);

        assertNotNull(result.getPreferencesJson());
        assertFalse(result.getPreferencesJson().contains("<script>"));
    }
}