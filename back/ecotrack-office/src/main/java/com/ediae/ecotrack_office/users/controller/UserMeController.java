package com.ediae.ecotrack_office.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.users.dto.UserMeRequestDto;
import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.service.UserService;

import jakarta.validation.Valid;

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

   @PutMapping("/me")
public ResponseEntity<UserModel> updateMe(@PathVariable Long id, @Valid @RequestBody UserMeRequestDto dto) {
    return ResponseEntity.ok(userService.updateMe(id, dto));
}
}