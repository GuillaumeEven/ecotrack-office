package com.ediae.ecotrack_office.users.dto;

import com.ediae.ecotrack_office.users.enums.Role;

public record UserCreateRequestDto(
    String email,
    String password,
    String firstName,
    String lastName,
    Role role,
    String cif
) {}
