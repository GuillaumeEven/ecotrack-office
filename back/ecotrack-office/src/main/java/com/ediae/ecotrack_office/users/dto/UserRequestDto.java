package com.ediae.ecotrack_office.users.dto;

import com.ediae.ecotrack_office.users.enums.Role;

public record UserRequestDto(
    String email,
    String password,
    String firstName,
    String lastName,
    Role role,
    Long organizationId,
    Boolean consentGiven,
    String preferencesJson
) {}
