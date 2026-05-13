package com.ediae.ecotrack_office.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;


@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

}
