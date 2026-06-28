package com.ediae.ecotrack_office.analiticsreport.dto;

public record AnaliticsReportRequestDto(
    Double co2SavingsKg,
    Double energySavingsEuros,
    Integer totalReservations,
    Integer confirmedCheckIns,
    Integer emptyRooms,
    Long organizationId
) {}