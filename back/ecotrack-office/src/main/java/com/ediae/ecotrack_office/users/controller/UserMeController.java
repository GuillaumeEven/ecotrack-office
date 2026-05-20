package com.ediae.ecotrack_office.users.controller;

import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserMeController {

    private final UserService userService;

    public UserMeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getMe(@RequestHeader("X-User-Id") Long userId) {
        UserModel model = userService.getUserById(userId);
        return ResponseEntity.ok(model.toResponseDto());
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateMe(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserMeRequestDto dto) {
        UserModel updated = userService.updateMe(userId, dto);
        return ResponseEntity.ok(updated.toResponseDto());
    }
}