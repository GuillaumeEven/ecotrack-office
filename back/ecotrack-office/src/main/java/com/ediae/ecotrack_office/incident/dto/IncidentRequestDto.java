package com.ediae.ecotrack_office.incident.dto;

public record IncidentRequestDto(
    String description,
    Long resourceId,
    Long userId
) {}