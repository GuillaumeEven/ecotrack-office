package com.ediae.ecotrack_office.integration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.ediae.ecotrack_office.auth.dto.LoginRequestDto;
import com.ediae.ecotrack_office.auth.dto.LoginResponseDto;
import com.ediae.ecotrack_office.integration.config.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class para todas las pruebas de integración.
 * Proporciona MockMvc, helpers para autenticación, y acceso a la base de datos de pruebas.
 *
 * Todos los tests de integración deben heredar de esta clase:
 * - Carga el contexto completo de Spring
 * - Usa base de datos H2 en memoria
 * - Proporciona métodos helper para login y autenticación
 */
@SpringBootTest
@ActiveProfiles({"test", "dev"})
@Transactional
@Import(TestConfig.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    /**
     * Helper: Realiza un login y retorna el token JWT
     *
     * Dado: credenciales válidas del usuario
     * Cuando: se envía una solicitud POST a /api/v1/auth/login
     * Entonces: retorna un token JWT válido
     */
    protected String loginAndGetToken(String email, String password) throws Exception {
        // Crear el DTO de solicitud con las credenciales
        LoginRequestDto loginRequest = new LoginRequestDto(email, password);
        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andReturn();

        // Extraer el token del response
        String responseBody = result.getResponse().getContentAsString();
        LoginResponseDto loginResponse = objectMapper.readValue(responseBody, LoginResponseDto.class);

        return loginResponse.token();
    }

    /**
     * Helper: Crea una autenticación mock con un userId específico y roles
     *
     * Útil para endpoints que requieren autenticación pero no necesitamos
     * hacer un login real. El userId se pasa como Principal.
     */
    protected Authentication createAuthenticationWithUserId(Long userId, String... roles) {
        String[] finalRoles = roles.length > 0 ? roles : new String[]{"ROLE_USER"};

        return new Authentication() {
            @Override
            public String getName() {
                return userId.toString();
            }

            @Override
            public Object getPrincipal() {
                return userId;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return AuthorityUtils.createAuthorityList(finalRoles);
            }
        };
    }

    /**
     * Helper: Obtiene el post processor de seguridad para una autenticación
     *
     * Uso:
     * mockMvc.perform(
     *     MockMvcRequestBuilders.get("/api/v1/floors/status")
     *     .with(getAuthWithUserId(1L))
     * )
     */
    protected org.springframework.test.web.servlet.request.RequestPostProcessor getAuthWithUserId(Long userId) {
        return SecurityMockMvcRequestPostProcessors.authentication(createAuthenticationWithUserId(userId, "ROLE_USER"));
    }

    /**
     * Helper: Obtiene el post processor de seguridad para un usuario ADMIN
     *
     * Uso para endpoints que requieren rol ADMIN:
     * mockMvc.perform(
     *     MockMvcRequestBuilders.post("/api/v1/desks")
     *     .with(getAuthAdminWithUserId(1L))
     * )
     */
    protected org.springframework.test.web.servlet.request.RequestPostProcessor getAuthAdminWithUserId(Long userId) {
        return SecurityMockMvcRequestPostProcessors.authentication(createAuthenticationWithUserId(userId, "ROLE_ADMIN"));
    }
}

