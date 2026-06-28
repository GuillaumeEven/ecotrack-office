# Integration Tests - Backend EcoTrack

## 📋 Descripción

Este directorio contiene **pruebas de integración** para el backend de EcoTrack Office.

Estos tests verifican que los endpoints reales funcionan correctamente, integrando:
- Controladores HTTP
- Servicios de negocio
- Acceso a base de datos (H2 en memoria para tests)
- Autenticación y autorización
- Mapeos de DTOs

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

### Método 2: Login real (con token JWT)
Para tests del endpoint de login:

```java
String token = loginAndGetToken("admin@ecotrack.com", "password123");

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
        .with(getAuthWithUserId(1L))  // si necesita auth
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

Los tests usan **H2 en memoria** (configurado en `application-test.yml`):

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
```

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

### FloorControllerIntegrationTest  
- ✅ GET todos los pisos
- ✅ GET piso por ID
- ✅ GET pisos por organización
- ✅ **GET estado de pisos (método TOUCHY)** ← Especialmente importante
- ✅ Crear/actualizar/eliminar (ADMIN only)

### DeskControllerIntegrationTest
- ✅ CRUD de desks
- ✅ Desks por sala
- ✅ Estado de desks (ocupados/disponibles)
- ✅ Validación de permisos (ADMIN)

### ReservationControllerIntegrationTest
- ✅ Crear reservación
- ✅ Obtener mis reservaciones
- ✅ Actualizar/cancelar
- ✅ Validación de conflictos
- ✅ Validación de fecha (no pasadas)
- ✅ Privacidad (usuario solo ve sus propias reservaciones)

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
- [ ] Autenticación correcta (con `getAuthWithUserId()` o login)
- [ ] Sin hardcoded IDs (usar fixture factories si es posible)

## 🎓 Recursos

- [Spring Boot Testing Docs](https://spring.io/guides/gs/testing-web/)
- [MockMvc Documentation](https://spring.io/guides/tutorials/spring-security-and-angular-js/)
- [JSONPath Syntax](https://github.com/json-path/JsonPath)
- [H2 Database Documentation](https://h2database.com/)

---

**Hecho por:** Equipo de EcoTrack  
**Última actualización:** 28/06/2024  
**Versión:** 1.0
