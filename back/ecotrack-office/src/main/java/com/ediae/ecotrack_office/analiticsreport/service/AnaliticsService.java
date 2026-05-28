package com.ediae.ecotrack_office.analiticsreport.service;

import com.ediae.ecotrack_office.analiticsreport.AnaliticsReportEntity;
import com.ediae.ecotrack_office.analiticsreport.model.AnaliticsReportModel;
import com.ediae.ecotrack_office.analiticsreport.mapper.AnaliticsReportMapper;
import com.ediae.ecotrack_office.analiticsreport.repository.AnaliticsReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnaliticsService {

    private final AnaliticsReportRepository analiticsReportRepository;

    public AnaliticsService(AnaliticsReportRepository analiticsReportRepository) {
        this.analiticsReportRepository = analiticsReportRepository;
    }

    @Transactional(readOnly = true)
    public List<AnaliticsReportModel> getAllReports() {
        return analiticsReportRepository.findAll().stream()
                .map(AnaliticsReportMapper::toModel)
                .collect(Collectors.toList());
    }
}