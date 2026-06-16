Intento harmonizar el sistema de filtros por rol, de autorizaciones, de token, extendiendolo a todos los endpoints.

# Guía de Autenticación JWT - Sistema Centralizado de Roles

## 📋 Resumen Ejecutivo

Este documento describe la arquitectura completa de autenticación y autorización implementada en EcoTrack Office. Se ha consolidado un sistema de guards y filtros JWT que **extiende la seguridad a TODOS los endpoints** de la aplicación (no solo admin), permitiendo control granular por roles: `ADMIN`, `TECHNICIAN`, `EMPLOYEE`.

### 🎯 Cambio Principal
- **Antes:** Autenticación X-User-Id/X-User-Role (MVP inseguro)
- **Ahora:** JWT Bearer Token con validación centralizada en `RoleGuard`

---

## 🏗️ Arquitectura General

```mermaid
graph TB
    A[Cliente<br/>Angular/Postman] -->|1. POST /auth/login| B[AuthController]
    B -->|2. Valida credentials| C[AuthService]
    C -->|3. Genera JWT| D[JwtService]
    D -->|4. Retorna token| A
    
    A -->|5. GET /api/v1/recursos<br/>Authorization: Bearer token| E[JwtFilter]
    E -->|6. Valida + extrae userId/role| F[Authentication]
    F -->|7. Pasa auth a controller| G[ResourceController]
    G -->|8. Verifica permisos<br/>con RoleGuard| H[Endpoint ejecutado]
    
    H -->|9. Retorna datos| A
    
    style A fill:#e1f5ff
    style B fill:#fff3e0
    style E fill:#f3e5f5
    style H fill:#e8f5e9
```

---

## 1️⃣ FLUJO DE AUTENTICACIÓN (Backend)

### 1.1 Paso 1: Login y Generación de JWT

**Endpoint:** `POST /api/v1/auth/login`

```json
// REQUEST
{
  "email": "technician@ecotrack.local",
  "password": "SecurePass123!"
}

// RESPONSE (200 OK)
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 2,
  "email": "technician@ecotrack.local",
  "role": "TECHNICIAN"
}
```

**Código Backend (AuthController.java):**
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // ✅ PÚBLICO - No requiere autenticación
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
```

---

### 1.2 Paso 2: El JWT Token

El token contiene esta información encriptada:

```json
{
  "alg": "HS256",
  "typ": "JWT"
}.{
  "sub": "2",                    // userId
  "role": "TECHNICIAN",          // ADMIN | TECHNICIAN | EMPLOYEE
  "iat": 1686700000,             // Issued at
  "exp": 1686786400              // Expiration (24h después)
}.{
  "signature": "..."
}
```

---

### 1.3 Paso 3: Interceptor JWT (Backend)

**Archivo:** `JwtFilter.java`

```java
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        // 1️⃣ Extrae el token del header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // 2️⃣ Valida y extrae datos del token
            if (jwtService.isTokenValid(token)) {
                Long userId = jwtService.extractUserId(token);
                Role role = jwtService.extractRole(token);
                
                // 3️⃣ Crea Authentication con userId como principal
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        userId,                          // principal (userId)
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

## 2️⃣ FLUJO DE AUTORIZACIÓN (Controllers)

### 2.1 Acceso a Endpoints Protegidos

```mermaid
graph LR
    A[Cliente envía<br/>Authorization: Bearer token] -->|JwtFilter valida token| B{Token válido?}
    B -->|❌ No| C[401 Unauthorized]
    B -->|✅ Sí| D[RoleGuard verifica<br/>permisos]
    D -->|❌ No tiene rol| E[403 Forbidden]
    D -->|✅ Tiene rol| F[Endpoint ejecutado]
    
    style C fill:#ffebee
    style E fill:#fff3e0
    style F fill:#e8f5e9
```

### 2.2 Ejemplo: FloorController.java

```java
@RestController
@RequestMapping("/api/v1/floors")
public class FloorController {

    @Autowired
    private FloorService floorService;
    
    @Autowired
    private RoleGuard roleGuard;  // ← Inyectado centralmente

    // ✅ GET - Todos los usuarios autenticados pueden listar
    @GetMapping
    public ResponseEntity<List<FloorResponseDto>> getAllFloors(Authentication auth) {
        Long userId = roleGuard.getUserIdFromAuth(auth);
        System.out.println("GET /floors por userId: " + userId);
        
        List<FloorResponseDto> floors = new ArrayList<>();
        floorService.getFloors().forEach(floor -> 
            floors.add(floorMapper.toResponseDto(floor))
        );
        return ResponseEntity.ok(floors);
    }

    // 🔒 POST - Solo ADMIN
    @PostMapping
    public ResponseEntity<FloorResponseDto> createFloor(
            Authentication auth, 
            @RequestBody FloorRequestDto dto) {
        
        // ← Check: ¿Es ADMIN?
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Long userId = roleGuard.getUserIdFromAuth(auth);
        System.out.println("POST /floors por userId: " + userId);
        
        return ResponseEntity.ok(floorMapper.toResponseDto(
            floorService.createFloor(dto)
        ));
    }

    // 🔒 DELETE - Solo ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFloor(
            Authentication auth, 
            @PathVariable Long id) {
        
        if (!roleGuard.isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        floorService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### 2.3 RoleGuard - Utilidad Centralizada

**Archivo:** `RoleGuard.java`

```java
@Component
public class RoleGuard {

    // 📌 MODO 1: Retorna booleanos (para controllers)
    public Long getUserIdFromAuth(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    public boolean hasRole(Authentication auth, Role role) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public boolean isAdmin(Authentication auth) {
        return hasRole(auth, Role.ADMIN);
    }

    public boolean isTechnician(Authentication auth) {
        return hasRole(auth, Role.TECHNICIAN);
    }

    public boolean isEmployee(Authentication auth) {
        return hasRole(auth, Role.EMPLOYEE);
    }

    // 📌 MODO 2: Lanza excepciones (para servicios)
    public void requireRole(Authentication auth, Role role) {
        checkAuthenticated(auth);
        if (!hasRole(auth, role)) {
            throw new ForbiddenException(
                "Necesitas el rol " + role.name() + " para realizar esta acción."
            );
        }
    }

    private void checkAuthenticated(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("No estás autenticado.");
        }
    }
}
```

---

## 3️⃣ FRONTEND: Angular

### 3.1 Auth Interceptor

**Archivo:** `auth.interceptor.ts`

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1️⃣ Obtén el token del localStorage
  const token = localStorage.getItem('auth_token');
  
  // 2️⃣ Si existe, adjúntalo al header Authorization
  if (token) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }
  
  return next(req);
};
```

### 3.2 Login en Angular

```typescript
// auth.service.ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  
  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<LoginResponse>('/api/v1/auth/login', {
      email,
      password
    }).pipe(
      tap(response => {
        // 💾 Guarda el token en localStorage
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('user_id', response.userId.toString());
      })
    );
  }

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
  }
}
```

### 3.3 Consumir Endpoints Protegidos

```typescript
// floor.service.ts
@Injectable({ providedIn: 'root' })
export class FloorService {
  
  constructor(private http: HttpClient) {}

  // ✅ GET - Funciona para todos los autenticados
  getFloors() {
    return this.http.get<Floor[]>('/api/v1/floors');
  }

  // 🔒 POST - Solo ADMIN (el backend lo valida)
  createFloor(dto: FloorCreateDto) {
    return this.http.post<Floor>('/api/v1/floors', dto);
  }

  // 🔒 DELETE - Solo ADMIN
  deleteFloor(id: number) {
    return this.http.delete(`/api/v1/floors/${id}`);
  }
}
```

**El interceptor automáticamente adjunta el token a TODAS las requests** ✅

---

## 4️⃣ Postman: Testing

### 4.1 Paso 1: Login y Capturar Token

**Request:** `POST http://localhost:8080/api/v1/auth/login`

```json
{
  "email": "admin@ecotrack.local",
  "password": "AdminPass123!"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "role": "ADMIN"
}
```

### 4.2 Paso 2: Guardar Token en Variable de Entorno

En Postman, en la pestaña "Tests" del login:

```javascript
var jsonData = pm.response.json();
pm.environment.set("access_token", jsonData.token);
```

### 4.3 Paso 3: Usar Token en Otros Requests

**Request:** `GET http://localhost:8080/api/v1/floors`

**Headers:**
```
Authorization: Bearer {{access_token}}
```

Postman **automáticamente reemplazará** `{{access_token}}` con el token capturado ✅

### 4.4 Ejemplo Completo en Postman

```
GET /api/v1/floors
├─ Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
├─ Response 200 OK
└─ Body: [{ id: 1, name: "Floor 1", ... }]

POST /api/v1/floors
├─ Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
├─ Body: { "name": "Floor 3", "organizationId": 1 }
├─ Si usuario NO es ADMIN:
│  └─ Response 403 Forbidden ❌
├─ Si usuario ES ADMIN:
│  └─ Response 201 Created ✅
```

---

## 5️⃣ CAMBIOS INDUCIDOS - Guía de Migración

### 5.1 ¿Qué cambió en los Controllers?

**ANTES (MVP inseguro):**
```java
@GetMapping
public List<Floor> getFloors() {
    // ❌ Sin autenticación
    return floorService.getFloors();
}
```

**AHORA (JWT seguro):**
```java
@GetMapping
public ResponseEntity<List<FloorResponseDto>> getFloors(Authentication auth) {
    // ✅ Requiere Authentication
    Long userId = roleGuard.getUserIdFromAuth(auth);
    List<FloorResponseDto> floors = new ArrayList<>();
    floorService.getFloors().forEach(floor -> 
        floors.add(floorMapper.toResponseDto(floor))
    );
    return ResponseEntity.ok(floors);
}
```

**Cambios principales:**
1. ✅ Parámetro `Authentication auth` en TODOS los endpoints
2. ✅ Uso de `RoleGuard` para verificar permisos
3. ✅ Retorna `ResponseEntity` en lugar de objetos directos
4. ✅ Manejo de errores: 401 (no autenticado), 403 (no autorizado)

### 5.2 Patrón de Autorización por Rol

```java
// 1️⃣ GET - Todos los usuarios autenticados
@GetMapping
public ResponseEntity<List<Dto>> getAll(Authentication auth) {
    // Solo necesita que esté autenticado (JwtFilter lo verificó)
    Long userId = roleGuard.getUserIdFromAuth(auth);
    // ... lógica
}

// 2️⃣ POST/PUT/DELETE - Solo ADMIN
@PostMapping
public ResponseEntity<Dto> create(Authentication auth, @RequestBody Dto dto) {
    if (!roleGuard.isAdmin(auth)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    // ... lógica
}

// 3️⃣ Acceso a datos propios - EMPLOYEE ve solo sus datos
@GetMapping("/me")
public ResponseEntity<Dto> getMe(Authentication auth) {
    Long userId = roleGuard.getUserIdFromAuth(auth);
    // Retorna datos del usuario actual
    return ResponseEntity.ok(service.getById(userId));
}

// 4️⃣ Acceso condicional - Admin ve todo, Employee solo lo suyo
@GetMapping("/user/{id}")
public ResponseEntity<List<Dto>> getByUser(Authentication auth, @PathVariable Long id) {
    Long userId = roleGuard.getUserIdFromAuth(auth);
    if (!userId.equals(id) && !roleGuard.isAdmin(auth)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    // ... lógica
}
```

---

### 5.3 Ejemplo: ReservationController

**Antes:**
```java
@GetMapping("/user/{id}")
public List<ReservationResponseDto> getReservationsByUserId(@PathVariable Long id) {
    // ❌ Cualquiera podía ver las reservas de otros
    return service.getReservationsByUserId(id);
}
```

**Ahora:**
```java
@GetMapping("/user/{id}")
public ResponseEntity<List<ReservationResponseDto>> getReservationsByUserId(
        Authentication auth, 
        @PathVariable Long id) {
    
    Long userId = roleGuard.getUserIdFromAuth(auth);
    
    // ✅ Verifica que solo vea sus propias reservas (o es ADMIN)
    if (!userId.equals(id) && !roleGuard.isAdmin(auth)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // ... lógica
    return ResponseEntity.ok(dtos);
}
```

---

## 6️⃣ CHECKLIST DE MIGRACIÓN

### Para Desarrolladores Backend:

- [ ] ✅ Todos los endpoints reciben `Authentication auth`
- [ ] ✅ Inyectar `RoleGuard` en el controller
- [ ] ✅ Verificar permisos con `roleGuard.isAdmin()`, `roleGuard.isTechnician()`, etc.
- [ ] ✅ Retornar `ResponseEntity.status(HttpStatus.FORBIDDEN).build()` si no autorizado
- [ ] ✅ Compilar: `mvn clean compile` (sin errores)
- [ ] ✅ No usar más `AdminGuard` (está deprecado)

### Para Desarrolladores Frontend (Angular):

- [ ] ✅ `authInterceptor` configurado en `app.config.ts`
- [ ] ✅ Guardar token en `localStorage` después de login
- [ ] ✅ Los servicios automáticamente adjuntan el token (interceptor lo hace)
- [ ] ✅ Manejar errores 401 (redirigir a login) y 403 (mostrar mensaje)

### Para Testers (Postman):

- [ ] ✅ Hacer login y guardar token en variable de entorno
- [ ] ✅ Adjuntar token en header `Authorization: Bearer {{access_token}}`
- [ ] ✅ Probar endpoints con diferentes roles
- [ ] ✅ Verificar 403 cuando usuario no tiene permisos

---

## 7️⃣ CÓDIGOS DE RESPUESTA HTTP

| Código | Significado | Ejemplo |
|--------|-------------|---------|
| **200** | OK | GET `/api/v1/floors` exitoso |
| **201** | Created | POST `/api/v1/floors` exitoso |
| **204** | No Content | DELETE exitoso |
| **400** | Bad Request | JSON inválido |
| **401** | Unauthorized | Token falta o inválido |
| **403** | Forbidden | Usuario no tiene permiso |
| **404** | Not Found | Recurso no existe |
| **500** | Internal Error | Error en el servidor |

---

## 8️⃣ ERRORES COMUNES

### ❌ "401 Unauthorized"

```
Causa: Token no enviado o inválido
Solución:
1. Verificar que localStorage tiene el token
2. Verificar que el interceptor está configurado
3. Verificar Authorization header: "Bearer {token}"
```

### ❌ "403 Forbidden"

```
Causa: Usuario autenticado pero sin permisos
Solución:
1. Verificar que el usuario tiene el rol correcto
2. Leer error response para detalles
3. Usar un usuario ADMIN para testing
```

### ❌ "404 Not Found"

```
Causa: Endpoint no existe
Solución:
1. Verificar ruta: /api/v1/... (v1 es importante)
2. Verificar método HTTP (GET/POST/PUT/DELETE)
3. Verificar sintaxis en Postman
```

---

## 9️⃣ ENDPOINTS PROTEGIDOS POR ROL

### 📊 Matriz de Acceso

| Endpoint | GET | POST | PUT | DELETE | ADMIN | TECH | EMP | NOTAS |
|----------|-----|------|-----|--------|-------|------|-----|-------|
| `/floors` | ✅ | 🔒 | 🔒 | 🔒 | ✅ | ✅ | ✅ | Solo ADMIN puede modificar |
| `/rooms` | ✅ | 🔒 | 🔒 | 🔒 | ✅ | ✅ | ✅ | Solo ADMIN puede modificar |
| `/desks` | ✅ | 🔒 | 🔒 | 🔒 | ✅ | ✅ | ✅ | Solo ADMIN puede modificar |
| `/reservations` | ✅* | ✅ | ✅* | ✅* | ✅ | ✅ | ✅ | *EMPLOYEE solo su propia data |
| `/users` | 🔒 | 🔒 | 🔒 | 🔒 | ✅ | ❌ | ❌ | Admin-only |
| `/organizations` | ✅ | 🔒 | 🔒 | 🔒 | ✅ | ✅ | ✅ | Solo ADMIN puede modificar |
| `/incidents` | ✅ | 🔒 | 🔒 | 🔒 | ✅ | ✅ | ❌ | TECH puede crear, ADMIN gestiona |
| `/analytics-reports` | ✅ | 🔒 | 🔒 | 🔒 | ✅ | ✅ | ❌ | Reports solo para admin/tech |

**Leyenda:**
- ✅ = Acceso permitido
- 🔒 = Acceso restringido (ver notas)
- ❌ = Acceso denegado

---

## 🔟 DIAGRAMA DE SEGURIDAD COMPLETO

```mermaid
graph TB
    subgraph AUTH["🔐 Autenticación (JWT)"]
        LOGIN["POST /auth/login<br/>email + password"]
        TOKEN["Generate JWT<br/>userId + role"]
        STORE["localStorage.setItem<br/>auth_token"]
    end
    
    subgraph FILTER["🛡️ Filtro (Backend)"]
        EXTRACT["JwtFilter extrae<br/>Authorization header"]
        VALIDATE["Valida JWT<br/>con JwtService"]
        PRINCIPAL["Establece<br/>Authentication<br/>principal = userId"]
    end
    
    subgraph CONTROLLER["🎯 Controller"]
        PARAM["Recibe<br/>Authentication auth"]
        GUARD["RoleGuard verifica<br/>permisos"]
        EXECUTE["Ejecuta lógica"]
    end
    
    subgraph FRONTEND["📱 Frontend (Angular)"]
        INTERCEPTOR["HttpInterceptor<br/>adjunta token"]
        REQUEST["Envía request<br/>con Authorization"]
    end
    
    LOGIN -->|genera| TOKEN
    TOKEN -->|guarda| STORE
    STORE -->|incluye token en| INTERCEPTOR
    INTERCEPTOR -->|adjunta| REQUEST
    REQUEST -->|llega a| EXTRACT
    EXTRACT -->|valida| VALIDATE
    VALIDATE -->|crea| PRINCIPAL
    PRINCIPAL -->|pasa a| PARAM
    PARAM -->|verifica| GUARD
    GUARD -->|autoriza o rechaza| EXECUTE
    
    style AUTH fill:#e3f2fd
    style FILTER fill:#f3e5f5
    style CONTROLLER fill:#e8f5e9
    style FRONTEND fill:#fff3e0
```

---

## 📚 Archivos Modificados

### Backend
- `auth/controller/AuthController.java` - Login endpoint (público)
- `auth/filter/JwtFilter.java` - Extrae y valida JWT
- `shared/guard/RoleGuard.java` - Utilidad de autorización centralizada
- `assets/controller/FloorController.java` - Endpoints protegidos
- `assets/controller/RoomController.java` - Endpoints protegidos
- `assets/controller/DeskController.java` - Endpoints protegidos
- `reservation/controller/ReservationController.java` - Endpoints protegidos
- `users/controller/UserController.java` - Endpoints protegidos
- `organization/controller/OrganizationController.java` - Endpoints protegidos
- `incident/controller/IncidentController.java` - Endpoints protegidos
- `analiticsreport/controller/AnaliticsController.java` - Endpoints protegidos

### Frontend
- `interceptors/auth.interceptor.ts` - Adjunta token a requests
- `services/auth.service.ts` - Gestiona login/logout
- `app.config.ts` - Configura interceptor globalmente

---

## 🎓 Conclusión

Este sistema de autenticación y autorización:

✅ **Centraliza** la lógica de seguridad en `RoleGuard`
✅ **Extiende** la protección a TODOS los endpoints
✅ **Soporta** múltiples roles con control granular
✅ **Mantiene** coherencia entre frontend y backend
✅ **Facilita** testing y debugging con Postman
✅ **Escala** fácilmente a nuevas entidades y roles

---

## 📞 Preguntas Frecuentes

**P: ¿Qué pasa si el token expira?**
A: El frontend recibe 401. Implementar un refresh token flow o redirigir a login.

**P: ¿Cómo autenticar desde Postman?**
A: Login, guardar token en variable de entorno, adjuntar en Authorization header.

**P: ¿Puedo usar diferentes roles?**
A: Sí, añade valores a la enum `Role` y utiliza `roleGuard.hasRole(auth, Role.CUSTOM)`.

**P: ¿El interceptor se aplica a TODOS los requests?**
A: Sí, excepto si lo excluyes explícitamente en la configuración.

---

**Documento creado:** 13 de Junio, 2026
**Rama:** `chore/improve-auth-and-adapt-controllers`
**Referencia:** Sistema JWT centralizado con RoleGuard
