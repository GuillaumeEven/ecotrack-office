package com.ediae.ecotrack_office.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.ediae.ecotrack_office.auth.dto.LoginRequestDto;
import com.ediae.ecotrack_office.integration.AbstractIntegrationTest;

/**
 * Pruebas de integración para AuthController
 * 
 * Estos tests verifican el flujo completo de autenticación:
 * - Login con credenciales válidas
 * - Validación de email inválido
 * - Validación de campos requeridos
 * - Token JWT generado correctamente
 */
@DisplayName("AuthController Integration Tests")
public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    /**
     * Test: Login exitoso con credenciales válidas
     * 
     * Dado: un usuario con email y contraseña válidos en la BD
     * Cuando: se envía una solicitud POST a /api/v1/auth/login
     * Entonces: retorna 200 OK con un token JWT válido
     */
    @Test
    @DisplayName("Debería retornar 200 y token válido cuando login es exitoso")
    public void testLoginSuccess() throws Exception {
        // Para este test, necesitaríamos datos pre-poblados en la BD
        // Por ahora mostramos la estructura del test
        
        LoginRequestDto loginRequest = new LoginRequestDto(
            "admin@ecotrack.local",
            "password"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.userId").isNumber())
            .andExpect(jsonPath("$.role").isNotEmpty())
            .andExpect(jsonPath("$.email").value("admin@ecotrack.local"));
    }

    /**
     * Test: Login falla con email inválido
     * 
     * Dado: un email con formato inválido
     * Cuando: se envía una solicitud POST a /api/v1/auth/login
     * Entonces: retorna 400 Bad Request (validación)
     */
    @Test
    @DisplayName("Debería retornar 400 cuando email no es válido")
    public void testLoginWithInvalidEmail() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto(
            "not-an-email",
            "password123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test: Login falla cuando email está vacío
     * 
     * Dado: un request sin email
     * Cuando: se envía una solicitud POST a /api/v1/auth/login
     * Entonces: retorna 400 Bad Request
     */
    @Test
    @DisplayName("Debería retornar 400 cuando email está vacío")
    public void testLoginWithEmptyEmail() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto(
            "",
            "password123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test: Login falla cuando contraseña está vacía
     * 
     * Dado: un request sin contraseña
     * Cuando: se envía una solicitud POST a /api/v1/auth/login
     * Entonces: retorna 400 Bad Request
     */
    @Test
    @DisplayName("Debería retornar 400 cuando contraseña está vacía")
    public void testLoginWithEmptyPassword() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto(
            "admin@ecotrack.local",
            ""
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest());
    }
}
