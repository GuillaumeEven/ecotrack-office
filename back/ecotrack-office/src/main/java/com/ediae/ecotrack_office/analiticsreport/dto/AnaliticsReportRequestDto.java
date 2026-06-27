package com.ediae.ecotrack_office.analiticsreport.dto;

import java.time.LocalDate;

public record AnaliticsReportRequestDto(
    LocalDate dateReport
) {}