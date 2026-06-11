package com.ediae.ecotrack_office.analiticsreport.dto;

import java.time.LocalDateTime;

public record AnaliticsReportResponseDto(
    Long id,
    Double co2SavingsKg,
    Double energySavingsEuros,
    Integer totalReservations,
    Integer confirmedCheckIns,
    Integer emptyRooms,
    LocalDateTime generatedAt,
    Long organizationId
) {}