package com.ediae.ecotrack_office.analyticsreport.dto;

public record AnalyticsReportRequestDto(
    Double co2SavingsKg,
    Double energySavingsEuros,
    Integer totalReservations,
    Integer confirmedCheckIns,
    Integer emptyRooms,
    Long organizationId
) {}