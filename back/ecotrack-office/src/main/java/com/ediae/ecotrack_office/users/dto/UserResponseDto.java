package com.ediae.ecotrack_office.users.dto;

import java.time.LocalDateTime;

import com.ediae.ecotrack_office.users.enums.Role;

public record UserResponseDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    Role role,
    Long organizationId,
    Boolean isActive,
    Boolean consentGiven,
    String preferencesJson,
    LocalDateTime createdAt
) {}