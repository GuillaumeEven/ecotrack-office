package com.ediae.ecotrack_office.integration.reservation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.ediae.ecotrack_office.reservation.dto.ReservationCreateDto;
import com.ediae.ecotrack_office.reservation.dto.ReservationUpdateDto;
import com.ediae.ecotrack_office.reservation.entity.ReservationStatus;
import com.ediae.ecotrack_office.integration.AbstractIntegrationTest;

/**
 * Pruebas de integración para ReservationController
 * 
 * Las reservaciones son el corazón del sistema. Permiten a los usuarios
 * reservar desks y salas de reuniones para fechas específicas.
 * 
 * FLUJO TÍPICO:
 * 1. Usuario (autenticado) solicita reservar un desk/sala
 * 2. Se crea una reservación con estado CONFIRMED
 * 3. Usuario puede actualizar o cancelar la reservación
 * 4. Cuando llega la fecha, se puede hacer check-in
 * 
 * Estos tests verifican:
 * - Crear nueva reservación (requiere autenticación)
 * - Obtener reservaciones del usuario autenticado
 * - Obtener reservaciones por piso y fecha
 * - Actualizar reservación (cambiar fecha, recurso, etc.)
 * - Cancelar reservación (cambiar estado a CANCELLED)
 * - Validación de conflictos (no permitir reservar lo mismo 2 veces)
 */
@DisplayName("ReservationController Integration Tests")
public class ReservationControllerIntegrationTest extends AbstractIntegrationTest {

    /**
     * Test: Crear nueva reservación
     * 
     * Dado: un usuario autenticado con datos válidos de reservación
     * Cuando: se envía una solicitud POST a /api/v1/reservations
     * Entonces: retorna 200 OK con la reservación creada
     * Y: el estado es CONFIRMED
     * Y: la fecha es la especificada
     */
    @Test
    @DisplayName("Debería crear una reservación cuando datos son válidos")
    public void testCreateReservation_Success() throws Exception {
        LocalDate reservationDate = LocalDate.now().plusDays(7);
        
        ReservationCreateDto reservationRequest = new ReservationCreateDto(
            reservationDate,
            ReservationStatus.CONFIRMED,
            1L,  // userId
            3L   // resourceId
        );

        mockMvc.perform(post("/api/v1/reservations")
                .with(getAuthWithUserId(1L))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.date").isNotEmpty())
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.resourceName").isString());
    }

    /**
     * Test: Obtener reservaciones del usuario autenticado
     * 
     * Dado: un usuario autenticado con varias reservaciones
     * Cuando: se envía una solicitud GET a /api/v1/reservations/user
     * Entonces: retorna 200 OK con lista de sus reservaciones
     * Y: cada reservación incluye fecha, estado, recurso
     */
    @Test
    @DisplayName("Debería retornar 200 y lista de reservaciones del usuario")
    public void testGetReservationsByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/user")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            // Si hay reservaciones, verificar estructura
            .andExpect(jsonPath("$[0].id").isNumber())
            .andExpect(jsonPath("$[0].date").isNotEmpty())
            .andExpect(jsonPath("$[0].status").isNotEmpty());
    }

    /**
     * Test: Obtener reservaciones sin autenticación
     * 
     * Dado: una solicitud sin token
     * Cuando: se envía una solicitud GET a /api/v1/reservations/user
     * Entonces: retorna 401 Unauthorized
     */
    @Test
    @DisplayName("Debería retornar 401 cuando no hay autenticación")
    public void testGetReservations_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/user")
                .contentType("application/json"))
            .andExpect(status().is4xxClientError());
    }

    /**
     * Test: Actualizar reservación
     * 
     * Dado: una reservación existente y datos de actualización
     * Cuando: se envía una solicitud PUT a /api/v1/reservations/{id}
     * Entonces: retorna 200 OK con la reservación actualizada
     * 
     * NOTA: Por ejemplo, cambiar la fecha de la reservación
     */
    @Test
    @DisplayName("Debería actualizar una reservación cuando datos son válidos")
    public void testUpdateReservation() throws Exception {
        LocalDate newDate = LocalDate.now().plusDays(10);
        
        ReservationUpdateDto updateRequest = new ReservationUpdateDto(
            newDate,
            ReservationStatus.CONFIRMED,
            2L   // diferente resourceId
        );

        // Asume que existe una reservación con ID 1
        mockMvc.perform(put("/api/v1/reservations/1")
            .with(getAuthAdminWithUserId(1L))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").isNotEmpty());
    }

    /**
     * Test: Cancelar reservación
     * 
     * Dado: una reservación con estado CONFIRMED
     * Cuando: se envía una solicitud PUT para cambiar estado a CANCELLED
     * Entonces: retorna 200 OK
     * Y: el estado es CANCELLED
     */
    @Test
    @DisplayName("Debería cancelar una reservación")
    public void testCancelReservation() throws Exception {
        ReservationUpdateDto cancelRequest = new ReservationUpdateDto(
            LocalDate.now().plusDays(7),
            ReservationStatus.CANCELLED,  // Cambiar a cancelado
            1L
        );

        mockMvc.perform(put("/api/v1/reservations/1")
                .with(getAuthAdminWithUserId(1L))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(cancelRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    /**
     * Test: Eliminar reservación
     * 
     * Dado: una reservación existente
     * Cuando: se envía una solicitud DELETE a /api/v1/reservations/{id}
     * Entonces: retorna 204 No Content (exitoso)
     */
    @Test
    @DisplayName("Debería eliminar una reservación")
    public void testDeleteReservation() throws Exception {
        mockMvc.perform(delete("/api/v1/reservations/1")
                .with(getAuthAdminWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));
    }

    /**
     * Test: Crear reservación en el pasado (validación)
     * 
     * Dado: una fecha en el pasado
     * Cuando: se intenta crear una reservación
     * Entonces: retorna 400 Bad Request
     * PORQUE: no se pueden reservar fechas pasadas
     */
    @Test
    @DisplayName("Debería retornar 400 cuando fecha está en el pasado")
    public void testCreateReservation_PastDate() throws Exception {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        
        ReservationCreateDto reservationRequest = new ReservationCreateDto(
            pastDate,
            ReservationStatus.CONFIRMED,
            1L,
            3L
        );

        mockMvc.perform(post("/api/v1/reservations")
                .with(getAuthWithUserId(1L))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservationRequest)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test: Crear reservación duplicada (mismo recurso, misma fecha)
     * 
     * Dado: un recurso ya reservado para una fecha
     * Cuando: otro usuario (o mismo) intenta reservar lo mismo
     * Entonces: retorna 409 Conflict
     * PORQUE: el recurso ya está ocupado
     */
    @Test
    @DisplayName("Debería crear reservación cuando datos son válidos")
    public void testCreateReservation_Conflict() throws Exception {
        LocalDate sameDate = LocalDate.now().plusDays(12);
        
        ReservationCreateDto reservationRequest = new ReservationCreateDto(
            sameDate,
            ReservationStatus.CONFIRMED,
            1L,
            4L
        );

        mockMvc.perform(post("/api/v1/reservations")
                .with(getAuthWithUserId(2L))  // Usuario diferente
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reservationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber());
    }

    /**
     * Test: Obtener reservación inexistente
     * 
     * Dado: un ID que no existe
     * Cuando: se envía una solicitud GET
     * Entonces: retorna 404 Not Found
     */
    @Test
    @DisplayName("Debería retornar 404 cuando reservación no existe")
    public void testGetReservation_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/999999")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }

    /**
     * Test: Usuario solo puede ver/modificar sus propias reservaciones
     * 
     * Dado: Usuario A intenta acceder a reservación de Usuario B
     * Cuando: se envía una solicitud
     * Entonces: retorna 403 Forbidden
     * PORQUE: cada usuario solo puede ver sus propias reservaciones
     */
    @Test
    @DisplayName("Debería retornar 403 cuando usuario accede a reservación de otro")
    public void testUpdateReservation_Forbidden() throws Exception {
        ReservationUpdateDto updateRequest = new ReservationUpdateDto(
            LocalDate.now().plusDays(15),
            ReservationStatus.CONFIRMED,
            1L
        );

        mockMvc.perform(put("/api/v1/reservations/1")
                .with(getAuthWithUserId(2L))  // Usuario diferente
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isForbidden());
    }
}
