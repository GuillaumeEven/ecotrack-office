package com.ediae.ecotrack_office.users.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.shared.dto.PageResponseDto;
import com.ediae.ecotrack_office.shared.guard.AdminGuard;
import com.ediae.ecotrack_office.users.dto.ChangePasswordRequestDto;
import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.dto.UserRequestDto;
import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AdminGuard adminGuard;

    public UserController(UserService userService, AdminGuard adminGuard) {
        this.userService = userService;
        this.adminGuard = adminGuard;
    }

    // ─── Endpoints /me (cualquier usuario autenticado) ───────────────────────

    // GET /api/v1/users/me
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId).toResponseDto());
    }

    // PATCH /api/v1/users/me
    // Solo puede modificar firstName, lastName, email, consentGiven y preferencesJson
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMe(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserMeRequestDto dto) {
        return ResponseEntity.ok(userService.updateMe(userId, dto).toResponseDto());
    }

    // PATCH /api/v1/users/me/password
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        userService.changePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }

    // ─── Endpoints de administración (solo ADMIN) ─────────────────────────────

    // GET /api/v1/users?organizationId=1&page=0&size=20
    @GetMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> getUsers(
            @RequestParam Long organizationId,
            Pageable pageable) {
        adminGuard.requireAdmin();
        return ResponseEntity.ok(userService.getUsersByOrganization(organizationId, pageable));
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        adminGuard.requireAdmin();
        return ResponseEntity.ok(userService.getUserById(id).toResponseDto());
    }

    // POST /api/v1/users
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto dto) {
        adminGuard.requireAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto).toResponseDto());
    }

    // PUT /api/v1/users/{id}
    // Puede modificar email, password, firstName, lastName y rol
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto dto) {
        adminGuard.requireAdmin();
        return ResponseEntity.ok(userService.updateUser(id, dto).toResponseDto());
    }

    // PATCH /api/v1/users/{id}/deactivate
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminGuard.requireAdmin();
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/users/{id}
    // GDPR erasure — no borra la fila
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminGuard.requireAdmin();
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}