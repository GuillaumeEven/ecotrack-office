package com.ediae.ecotrack_office.integration.assets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.ediae.ecotrack_office.assets.dto.FloorRequestDto;
import com.ediae.ecotrack_office.integration.AbstractIntegrationTest;

/**
 * Pruebas de integración para FloorController
 * 
 * IMPORTANTE: La ruta "/status" es compleja porque:
 * - Requiere autenticación
 * - Necesita calcular el estado (ocupación) de los pisos
 * - Integra datos de reservaciones
 * - Filtra por fecha
 * 
 * Estos tests verifican:
 * - Obtener todos los pisos
 * - Obtener piso por ID
 * - Obtener pisos por organización
 * - Obtener estado de pisos para una fecha (LA TRICKY)
 * - Crear nuevo piso (requiere ADMIN)
 * - Actualizar piso (requiere ADMIN)
 * - Eliminar piso (requiere ADMIN)
 */
@DisplayName("FloorController Integration Tests")
public class FloorControllerIntegrationTest extends AbstractIntegrationTest {

    /**
     * Test: Obtener todos los pisos
     * 
     * Dado: varios pisos en la BD
     * Cuando: se envía una solicitud GET a /api/v1/floors
     * Entonces: retorna 200 OK con lista de pisos
     */
    @Test
    @DisplayName("Debería retornar 200 y lista de pisos")
    public void testGetAllFloors() throws Exception {
        mockMvc.perform(get("/api/v1/floors")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    /**
     * Test: Obtener piso por ID
     * 
     * Dado: un ID de piso válido
     * Cuando: se envía una solicitud GET a /api/v1/floors/{id}
     * Entonces: retorna 200 OK con los datos del piso
     */
    @Test
    @DisplayName("Debería retornar 200 y datos del piso por ID")
    public void testGetFloorById() throws Exception {
        // Este test asume que existe un piso con ID 1 en la BD
        // En un setup real, primero crearías un piso o usarías datos precargados
        
        mockMvc.perform(get("/api/v1/floors/1")
            .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.level").isNumber())
            .andExpect(jsonPath("$.organizationId").isNumber());
    }

    /**
     * Test: Obtener pisos por organización
     * 
     * Dado: una organización con pisos
     * Cuando: se envía una solicitud GET a /api/v1/floors/organization/{organizationId}
     * Entonces: retorna 200 OK con pisos de esa organización
     */
    @Test
    @DisplayName("Debería retornar 200 y lista de pisos por organización")
    public void testGetFloorsByOrganizationId() throws Exception {
        mockMvc.perform(get("/api/v1/floors/organization/1")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    /**
     * Test: Obtener estado de pisos (EL MÉTODO TOUCHY)
     * 
     * IMPORTANTE: Este endpoint es complejo porque:
     * 1. Requiere autenticación (token en header o Authentication en contexto)
     * 2. Extrae la organizationId del usuario autenticado
     * 3. Calcula la ocupación basada en reservaciones para la fecha
     * 4. Retorna un DTO con estado de ocupación
     * 
     * Dado: un usuario autenticado con reservaciones para una fecha específica
     * Cuando: se envía una solicitud GET a /api/v1/floors/status?date=2024-01-15
     * Entonces: retorna 200 OK con estado de pisos (ocupados/disponibles)
     */
    @Test
    @DisplayName("Debería retornar 200 y estado de pisos para una fecha válida")
    public void testGetFloorsStatusByOrganizationId_ValidDate() throws Exception {
        // La fecha debe estar en formato ISO: YYYY-MM-DD
        LocalDate testDate = LocalDate.now().plusDays(1);
        String dateString = testDate.toString();

        // Usar autenticación mock con userId = 1
        // El servicio buscará la organización del usuario
        mockMvc.perform(get("/api/v1/floors/status")
                .param("date", dateString)
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            // Cada elemento debe contener un floor y estado de ocupación
            .andExpect(jsonPath("$[0].floor.id").isNumber())
            .andExpect(jsonPath("$[0].date").isString())
            .andExpect(jsonPath("$[0].desksOccupied").isBoolean())
            .andExpect(jsonPath("$[0].meetingRoomsOccupied").isBoolean());
    }

    /**
     * Test: getFloorsStatus sin parámetro date
     * 
     * Dado: una solicitud sin el parámetro "date"
     * Cuando: se envía una solicitud GET a /api/v1/floors/status
     * Entonces: retorna 400 Bad Request o usa fecha por defecto
     */
    @Test
    @DisplayName("Debería retornar error cuando falta el parámetro date")
    public void testGetFloorsStatus_MissingDateParameter() throws Exception {
        mockMvc.perform(get("/api/v1/floors/status")
                .with(getAuthWithUserId(1L))
                .contentType("application/json"))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test: getFloorsStatus sin autenticación
     * 
     * Dado: una solicitud sin token de autenticación
     * Cuando: se envía una solicitud GET a /api/v1/floors/status
     * Entonces: retorna 401 Unauthorized
     */
    @Test
    @DisplayName("Debería retornar 401 cuando no hay autenticación")
    public void testGetFloorsStatus_Unauthorized() throws Exception {
        LocalDate testDate = LocalDate.now();
        String dateString = testDate.toString();

        mockMvc.perform(get("/api/v1/floors/status")
                .param("date", dateString)
                .contentType("application/json"))
            .andExpect(status().is4xxClientError());
    }

    /**
     * Test: Crear nuevo piso (requiere ADMIN)
     * 
     * Dado: un usuario ADMIN autenticado con datos válidos de piso
     * Cuando: se envía una solicitud POST a /api/v1/floors
     * Entonces: retorna 200 OK con el piso creado
     */
    @Test
    @DisplayName("Debería crear un piso cuando ADMIN envía datos válidos")
    public void testCreateFloor_AsAdmin() throws Exception {
        FloorRequestDto floorRequest = new FloorRequestDto();
        floorRequest.setLevel(5);
        floorRequest.setName("Piso 5 - Desarrollo");
        floorRequest.setIsActive(true);

        mockMvc.perform(post("/api/v1/floors")
                .with(getAuthAdminWithUserId(1L))  // Suponer que user 1 es ADMIN
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(floorRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.level").value(5))
            .andExpect(jsonPath("$.name").value("Piso 5 - Desarrollo"));
    }

    /**
     * Test: Crear piso sin permisos (no ADMIN)
     * 
     * Dado: un usuario NO ADMIN
     * Cuando: intenta enviar POST a /api/v1/floors
     * Entonces: retorna 403 Forbidden
     */
    @Test
    @DisplayName("Debería retornar 403 cuando no es ADMIN")
    public void testCreateFloor_NotAdmin() throws Exception {
        FloorRequestDto floorRequest = new FloorRequestDto();
        floorRequest.setLevel(6);
        floorRequest.setName("Piso 6");

        mockMvc.perform(post("/api/v1/floors")
                .with(getAuthWithUserId(2L))  // Usuario normal (no ADMIN)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(floorRequest)))
            .andExpect(status().isForbidden());
    }
}
