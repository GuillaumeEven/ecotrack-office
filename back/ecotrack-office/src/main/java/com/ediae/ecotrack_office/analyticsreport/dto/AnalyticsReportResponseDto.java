package com.ediae.ecotrack_office.analyticsreport.dto;

import java.time.LocalDateTime;

public record AnalyticsReportResponseDto(
    Long id,
    Double co2SavingsKg,
    Double energySavingsEuros,
    Integer totalReservations,
    Integer confirmedCheckIns,
    Integer emptyRooms,
    LocalDateTime generatedAt,
    Long organizationId
) {}