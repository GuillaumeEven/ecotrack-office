package com.ediae.ecotrack_office.users.controller;

import com.ediae.ecotrack_office.shared.context.RequestContext;
import com.ediae.ecotrack_office.shared.dto.PageResponseDto;
import com.ediae.ecotrack_office.shared.guard.AdminGuard;
import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.dto.UserRequestDto;
import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.service.UserService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AdminGuard adminGuard;

    public UserController(UserService userService, AdminGuard adminGuard) {
        this.userService = userService;
        this.adminGuard = adminGuard;
    }

    // GET /api/v1/users?organizationId=1&page=0&size=20
    // Solo ADMIN
    @GetMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> getUsers(
            @RequestParam Long organizationId,
            Pageable pageable) {
        adminGuard.requireAdmin();
        return ResponseEntity.ok(userService.getUsersByOrganization(organizationId, pageable));
    }

    // GET /api/v1/users/{id}
    // Solo ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        adminGuard.requireAdmin();
        return ResponseEntity.ok(userService.getUserById(id).toResponseDto());
    }

    // POST /api/v1/users
    // Solo ADMIN
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto dto) {
        adminGuard.requireAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto).toResponseDto());
    }

    // PUT /api/v1/users/{id}
    // Solo ADMIN — puede modificar nombre, email y rol
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDto dto) {
        adminGuard.requireAdmin();
        return ResponseEntity.ok(userService.updateUser(id, dto).toResponseDto());
    }

    // PATCH /api/v1/users/{id}/deactivate
    // Solo ADMIN
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminGuard.requireAdmin();
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/users/{id}
    // Solo ADMIN — GDPR erasure, no borra la fila
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminGuard.requireAdmin();
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/v1/users/me
    // Cualquier usuario autenticado — solo sus propios datos
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMe(@RequestBody UserMeRequestDto dto) {
        // El id viene del contexto, no de la URL
        // Así un usuario nunca puede editar los datos de otro
        Long actorId = RequestContext.getUserId();
        return ResponseEntity.ok(userService.updateMe(actorId, dto).toResponseDto());
    }
}