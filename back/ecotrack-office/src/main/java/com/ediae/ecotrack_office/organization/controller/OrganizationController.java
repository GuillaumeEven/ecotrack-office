package com.ediae.ecotrack_office.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ediae.ecotrack_office.organization.dto.OrganizationResponseDto;
import com.ediae.ecotrack_office.organization.mapper.OrganizationMapper;
import com.ediae.ecotrack_office.organization.service.OrganizationService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET,
        RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/ecotrack-api/v1")
public class OrganizationController {

    @Autowired
    private OrganizationService service;

    @GetMapping("/organization/{id}")
    public OrganizationResponseDto getOrganizationById (@PathVariable Long id) {

        return OrganizationMapper.toResponseDto(service.getOrganizationById(id));
    }

}
