# Integration Tests - Backend EcoTrack

## 📋 Descripción

Este directorio contiene **pruebas de integración** para el backend de EcoTrack Office.

Estos tests verifican que los endpoints reales funcionan correctamente, integrando:
- Controladores HTTP
- Servicios de negocio
- Acceso a base de datos (H2 en memoria para tests)
- Autenticación y autorización
- Mapeos de DTOs

Actualmente la configuración de tests usa:
- Perfiles activos: `test` + `dev`
- H2 en memoria con compatibilidad MySQL (`MODE=MySQL`)
- `DataLoader` del perfil `dev` para sembrar datos base

## 🏗️ Estructura

```
src/test/java/com/ediae/ecotrack_office/integration/
├── AbstractIntegrationTest.java       # Clase base para todos los tests
├── auth/
│   └── AuthControllerIntegrationTest.java         # Login y autenticación
├── assets/
│   ├── FloorControllerIntegrationTest.java        # Pisos (GET, POST, PUT, DELETE)
│   └── DeskControllerIntegrationTest.java         # Desks (GET, POST, PUT, DELETE)
└── reservation/
    └── ReservationControllerIntegrationTest.java  # Reservaciones (CRUD completo)
```

## 🚀 Cómo ejecutar los tests

### Ejecutar todos los tests
```bash
cd back/ecotrack-office
mvn test
```

### Ejecutar un test específico
```bash
mvn test -Dtest=AuthControllerIntegrationTest
mvn test -Dtest=FloorControllerIntegrationTest
mvn test -Dtest=DeskControllerIntegrationTest
mvn test -Dtest=ReservationControllerIntegrationTest
```

### Ejecutar un método de test específico
```bash
mvn test -Dtest=AuthControllerIntegrationTest#testLoginSuccess
```

### Ver cobertura de tests
```bash
mvn test jacoco:report
```

## 📚 Patrón de Tests: Given-When-Then

Todos los tests siguen este patrón en sus comentarios en español:

```java
/**
 * Dado: (el estado inicial/setup)
 * Cuando: (la acción que realizamos)
 * Entonces: (el resultado esperado)
 */
```

**Ejemplo:**
```java
/**
 * Dado: un usuario autenticado
 * Cuando: hace una solicitud GET a /api/v1/floors
 * Entonces: retorna 200 OK con lista de pisos
 */
@Test
public void testGetAllFloors() throws Exception {
    mockMvc.perform(get("/api/v1/floors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
}
```

## 🔐 Autenticación en Tests

### Método 1: Helper con userId
Para tests que NO necesitan un login real:

```java
mockMvc.perform(get("/api/v1/floors/status")
    .param("date", "2024-12-25")
    .with(getAuthWithUserId(1L))  // User ID 1 autenticado
    .contentType("application/json"))
    .andExpect(status().isOk());
```

### Método 1b: Helper ADMIN
Para endpoints que exigen rol ADMIN:

```java
mockMvc.perform(post("/api/v1/desks")
    .with(getAuthAdminWithUserId(1L))
    .contentType("application/json")
    .content(objectMapper.writeValueAsString(dto)))
    .andExpect(status().isOk());
```

### Método 2: Login real (con token JWT)
Para tests del endpoint de login:

```java
String token = loginAndGetToken("admin@ecotrack.local", "password");

mockMvc.perform(get("/api/v1/floors")
    .header("Authorization", "Bearer " + token)
    .contentType("application/json"))
    .andExpect(status().isOk());
```

## 💡 Tips para escribir nuevos tests

### 1. Heredar de AbstractIntegrationTest
```java
public class MyControllerIntegrationTest extends AbstractIntegrationTest {
    // Tu test aquí
}
```

### 2. Usar MockMvc para hacer requests
```java
mockMvc.perform(
    get("/api/v1/endpoint")  // POST, PUT, DELETE también disponibles
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto))
    .with(getAuthWithUserId(1L))  // usuario autenticado
    // .with(getAuthAdminWithUserId(1L))  // si requiere ADMIN
)
.andExpect(status().isOk())  // Verificar HTTP status
.andExpect(jsonPath("$.campo").value("esperado"));  // Verificar respuesta JSON
```

### 3. Comentarios explicativos en español
Siempre explica **qué** se está testando:

```java
/**
 * Test: Crear nuevo piso (requiere permisos ADMIN)
 * 
 * Dado: un usuario ADMIN con datos válidos
 * Cuando: envía POST a /api/v1/floors
 * Entonces: retorna 200 y crea el piso
 */
```

### 4. Testear happy path + 1-2 edge cases
No testees todos los escenarios posibles. Enfócate en:
- ✅ El caso normal (happy path)
- ✅ Un error importante (validación, seguridad, conflicto)

**Ejemplo bueno:**
- ✅ testCreateFloor_Success (happy path)
- ✅ testCreateFloor_NotAdmin (seguridad)
- ❌ testCreateFloor_NullName (demasiado granular)

## 🗄️ Base de Datos de Test

Los tests usan **H2 en memoria** con compatibilidad MySQL (configurado en `application-test.yml` / `application-test.properties`):

```yaml
spring:
  datasource:
        url: jdbc:h2:mem:testdb;MODE=MySQL
    driver-class-name: org.h2.Driver
```

Además, la clase base usa:

```java
@ActiveProfiles({"test", "dev"})
```

Esto permite que el `DataLoader` (perfil `dev`) siembre usuarios, pisos, salas, desks, reservas e incidentes que usan los tests.

**Ventajas:**
- ✅ Rápido (en memoria)
- ✅ Aislado (cada test es independiente)
- ✅ No necesita MySQL corriendo
- ✅ Rollback automático después de cada test

## 🎯 Qué Testeamos

### AuthControllerIntegrationTest
- ✅ Login exitoso con credenciales válidas
- ✅ Validación de email inválido
- ✅ Validación de campos requeridos
- ℹ️ Credenciales seed actuales: `admin@ecotrack.local` / `password`

### FloorControllerIntegrationTest  
- ✅ GET todos los pisos
- ✅ GET piso por ID
- ✅ GET pisos por organización
- ✅ **GET estado de pisos (método TOUCHY)** ← Especialmente importante
- ✅ Crear/actualizar/eliminar (ADMIN only)

### DeskControllerIntegrationTest
- ✅ CRUD de desks
- ✅ Desks por sala
- ✅ Incidentes por desk (`GET /api/v1/desks/{id}/incidents`)
- ✅ Validación de permisos (ADMIN)

### ReservationControllerIntegrationTest
- ✅ Crear reservación
- ✅ Obtener mis reservaciones
- ✅ Actualizar/cancelar
- ✅ Validación de fecha (no pasadas)
- ✅ Privacidad (usuario solo ve sus propias reservaciones)

> Nota: algunos tests reflejan el comportamiento actual del backend aunque no sea el ideal REST (por ejemplo, `DELETE /reservations/{id}` retorna `200` con booleano).

## 📊 Cobertura

Objetivo: **70-80%** de cobertura en lógica de negocio.

NO es necesario:
- ❌ 100% de cobertura (imposible y contraproducente)
- ❌ Testear getters/setters triviales
- ❌ Testear todas las combinaciones de errores

SÍ es importante:
- ✅ Flows críticos (crear reservación, login)
- ✅ Validaciones importantes
- ✅ Reglas de negocio (conflictos, permisos)
- ✅ Integraciones (Controller → Service → DB)

## 🐛 Debug de Tests Fallidos

### Ver el response completo
```java
.andDo(print())  // Añade esta línea
```

Ejemplo:
```java
mockMvc.perform(get("/api/v1/floors"))
    .andDo(print())  // Imprime request + response
    .andExpect(status().isOk());
```

### Ver logs durante tests
```bash
mvn test -Ddebug  # Modo debug
```

## 📝 Checklist para Nuevo Test

- [ ] Test hereda de `AbstractIntegrationTest`
- [ ] Nombre descriptivo: `testActionDescription`
- [ ] Comentarios en español explicando Dado-Cuando-Entonces
- [ ] Testea happy path
- [ ] Testea al menos 1 error/validación
- [ ] Verifica HTTP status (`isOk()`, `isNotFound()`, etc.)
- [ ] Verifica respuesta JSON con `jsonPath()`
- [ ] Autenticación correcta (`getAuthWithUserId()`, `getAuthAdminWithUserId()` o login)
- [ ] Si usas IDs hardcodeados, documenta que provienen del seed (`DataLoader`)

## 🎓 Recursos

- [Spring Boot Testing Docs](https://spring.io/guides/gs/testing-web/)
- [MockMvc Documentation](https://spring.io/guides/tutorials/spring-security-and-angular-js/)
- [JSONPath Syntax](https://github.com/json-path/JsonPath)
- [H2 Database Documentation](https://h2database.com/)

---

**Hecho por:** Equipo de EcoTrack  
**Última actualización:** 28/06/2026  
**Versión:** 1.1
