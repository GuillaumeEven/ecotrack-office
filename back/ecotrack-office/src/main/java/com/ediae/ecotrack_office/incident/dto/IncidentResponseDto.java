package com.ediae.ecotrack_office.incident.dto;

import com.ediae.ecotrack_office.incident.enums.IncidentStatus;
import java.time.LocalDateTime;

public record IncidentResponseDto(
    Long id,
    String description,
    IncidentStatus status,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt,
    Long userId,
    Long resourceId
) {}