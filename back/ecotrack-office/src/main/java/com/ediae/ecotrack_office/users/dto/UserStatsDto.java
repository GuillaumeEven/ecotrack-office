package com.ediae.ecotrack_office.users.dto;

public record UserStatsDto(
    long totalUsers,
    long activeUsers,
    long newThisMonth
) {}