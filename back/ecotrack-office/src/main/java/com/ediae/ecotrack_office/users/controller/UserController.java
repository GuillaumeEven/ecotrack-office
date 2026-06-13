package com.ediae.ecotrack_office.users.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserResponseDto> getMe(Authentication auth) {
        // El JwtFilter ya verificó el token y guardó el userId como principal
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(userId).toResponseDto());
    }

    // PATCH /api/v1/users/me
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMe(
            Authentication auth,
            @Valid @RequestBody UserMeRequestDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(userService.updateMe(userId, dto).toResponseDto());
    }

    // PATCH /api/v1/users/me/password
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication auth,
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        Long userId = (Long) auth.getPrincipal();
        userService.changePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }

    // ─── Endpoints de administración (solo ADMIN) ─────────────────────────────

    // GET /api/v1/users?organizationId=1&page=0&size=20
    @GetMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> getUsers(
            Authentication auth,
            @RequestParam Long organizationId,
            Pageable pageable) {
        adminGuard.requireAdmin(auth);
        return ResponseEntity.ok(userService.getUsersByOrganization(organizationId, pageable));
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            Authentication auth,
            @PathVariable Long id) {
        adminGuard.requireAdmin(auth);
        return ResponseEntity.ok(userService.getUserById(id).toResponseDto());
    }

    // POST /api/v1/users
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            Authentication auth,
            @Valid @RequestBody UserRequestDto dto) {
        adminGuard.requireAdmin(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto).toResponseDto());
    }

    // PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto dto) {
        adminGuard.requireAdmin(auth);
        return ResponseEntity.ok(userService.updateUser(id, dto).toResponseDto());
    }

    // PATCH /api/v1/users/{id}/deactivate
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            Authentication auth,
            @PathVariable Long id) {
        adminGuard.requireAdmin(auth);
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            Authentication auth,
            @PathVariable Long id) {
        adminGuard.requireAdmin(auth);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}