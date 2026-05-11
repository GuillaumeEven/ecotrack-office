package com.ediae.ecotrack_office.users.controller;

import com.ediae.ecotrack_office.users.dto.UserRequestDto;
import com.ediae.ecotrack_office.users.dto.UserResponseDto;
import com.ediae.ecotrack_office.users.models.UserModel;
import com.ediae.ecotrack_office.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserModel model = userService.getUserById(id);
        return ResponseEntity.ok(model.toResponseDto());
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsersByOrganization(@RequestParam Long organizationId) {
        List<UserModel> models = userService.getUsersByOrganization(organizationId);
        List<UserResponseDto> response = new ArrayList<>();
        for (UserModel model : models) {
            response.add(model.toResponseDto());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto dto) {
        UserModel created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toResponseDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto dto) {
        UserModel updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated.toResponseDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
