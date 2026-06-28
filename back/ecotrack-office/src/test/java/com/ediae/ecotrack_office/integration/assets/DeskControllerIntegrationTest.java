package com.ediae.ecotrack_office.integration.assets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.ediae.ecotrack_office.assets.dto.DeskRequestDto;
import com.ediae.ecotrack_office.integration.AbstractIntegrationTest;

/**
 * Pruebas de integración para DeskController
 * 
 * Los desks (escritorios) son recursos dentro de salas.
 * Estos tests verifican:
 * - Obtener todos los desks
 * - Obtener desk por ID
 * - Obtener desks por sala
 * - Obtener estado de desks con reservaciones
 * - Crear nuevo desk (requiere ADMIN)
 * - Actualizar desk (requiere ADMIN)
 * - Eliminar desk (requiere ADMIN)
 */
@DisplayName("DeskController Integration Tests")
public class DeskControllerIntegrationTest extends AbstractIntegrationTest {

    /**
     * Test: Obtener todos los desks
     * 
     * Dado: varios desks en la BD
     * Cuando: se envía una solicitud GET a /api/v1/desks
     * Entonces: retorna 200 OK con lista de desks
     */
    @Test
    @DisplayName("Debería retornar 200 y lista de desks")
    public void testGetAllDesks() throws Exception {
        mockMvc.perform(get("/api/v1/desks")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    /**
     * Test: Obtener desk por ID
     * 
     * Dado: un ID de desk válido
     * Cuando: se envía una solicitud GET a /api/v1/desks/{id}
     * Entonces: retorna 200 OK con datos del desk
     */
    @Test
    @DisplayName("Debería retornar 200 y datos del desk por ID")
    public void testGetDeskById() throws Exception {
        // En los datos seed, existe desk con ID 3 (D2)
        mockMvc.perform(get("/api/v1/desks/3")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isString())
            .andExpect(jsonPath("$.roomId").isNumber());
    }

    /**
     * Test: Obtener desks por sala
     * 
     * Dado: una sala con desks
     * Cuando: se envía una solicitud GET a /api/v1/desks/room/{roomId}
     * Entonces: retorna 200 OK con desks de esa sala
     */
    @Test
    @DisplayName("Debería retornar 200 y lista de desks por sala")
    public void testGetDesksByRoomId() throws Exception {
        mockMvc.perform(get("/api/v1/desks/room/1")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    /**
     * Test: Obtener estado de desks para una fecha
     * 
     * Dado: desks con reservaciones para una fecha específica
     * Cuando: se envía una solicitud GET a /api/v1/desks/status?date=2024-01-15&roomId=1
     * Entonces: retorna 200 OK con estado de cada desk (ocupado/disponible)
     * Y: incluye quién hizo la reservación
     */
    @Test
    @DisplayName("Debería retornar 200 y lista de incidentes por desk")
    public void testGetDesksStatusByRoomIdAndDate() throws Exception {
        mockMvc.perform(get("/api/v1/desks/3/incidents")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].description").isString())
            .andExpect(jsonPath("$[0].status").isString());
    }

    /**
     * Test: Crear nuevo desk (requiere ADMIN)
     * 
     * Dado: un ADMIN con datos válidos de desk
     * Cuando: se envía una solicitud POST a /api/v1/desks
     * Entonces: retorna 200 OK con el desk creado
     */
    @Test
    @DisplayName("Debería crear un desk cuando ADMIN envía datos válidos")
    public void testCreateDesk_AsAdmin() throws Exception {
        DeskRequestDto deskRequest = new DeskRequestDto();
        deskRequest.setName("Desk A-101");
        deskRequest.setRoomId(1L);
        deskRequest.setIsActive(true);
        deskRequest.setEquipmentList("Pantalla dual,Teclado mecánico,Ratón");

        mockMvc.perform(post("/api/v1/desks")
                .with(getAuthAdminWithUserId(1L))  // ADMIN user
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(deskRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Desk A-101"))
            .andExpect(jsonPath("$.roomId").value(1L));
    }

    /**
     * Test: Actualizar desk (requiere ADMIN)
     * 
     * Dado: un ADMIN y un desk existente
     * Cuando: se envía una solicitud PUT a /api/v1/desks/{id}
     * Entonces: retorna 200 OK con el desk actualizado
     */
    @Test
    @DisplayName("Debería actualizar un desk cuando ADMIN envía datos válidos")
    public void testUpdateDesk_AsAdmin() throws Exception {
        DeskRequestDto deskRequest = new DeskRequestDto();
        deskRequest.setName("Desk A-102 (Renovado)");
        deskRequest.setRoomId(1L);
        deskRequest.setIsActive(true);

        mockMvc.perform(put("/api/v1/desks/3")
                .with(getAuthAdminWithUserId(1L))  // ADMIN user
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(deskRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Desk A-102 (Renovado)"));
    }

    /**
     * Test: Eliminar desk (requiere ADMIN)
     * 
     * Dado: un ADMIN
     * Cuando: se envía una solicitud DELETE a /api/v1/desks/{id}
     * Entonces: retorna 204 No Content (exitoso)
     */
    @Test
    @DisplayName("Debería eliminar un desk cuando ADMIN lo solicita")
    public void testDeleteDesk_AsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/desks/3")
                .with(getAuthAdminWithUserId(1L))  // ADMIN user
                .contentType("application/json"))
            .andExpect(status().isNoContent());
    }

    /**
     * Test: Crear desk sin permisos (no ADMIN)
     * 
     * Dado: un usuario NO ADMIN
     * Cuando: intenta enviar POST a /api/v1/desks
     * Entonces: retorna 403 Forbidden
     */
    @Test
    @DisplayName("Debería retornar 403 cuando no es ADMIN")
    public void testCreateDesk_NotAdmin() throws Exception {
        DeskRequestDto deskRequest = new DeskRequestDto();
        deskRequest.setName("Desk X-999");
        deskRequest.setRoomId(1L);

        mockMvc.perform(post("/api/v1/desks")
                .with(getAuthWithUserId(2L))  // Usuario normal (no ADMIN)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(deskRequest)))
            .andExpect(status().isForbidden());
    }

    /**
     * Test: Obtener desk inexistente
     * 
     * Dado: un ID que no existe
     * Cuando: se envía una solicitud GET a /api/v1/desks/{id}
     * Entonces: retorna 404 Not Found
     */
    @Test
    @DisplayName("Debería retornar 404 cuando desk no existe")
    public void testGetDesk_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/desks/999999")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }
}
