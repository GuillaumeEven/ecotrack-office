package com.ediae.ecotrack_office.auth.dto;

public record LoginResponseDto(
    String token,
    Long userId,
    String role
) {}