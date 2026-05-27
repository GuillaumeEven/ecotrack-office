package com.ediae.ecotrack_office.organization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.organization.entity.OrganizationEntity;


@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

    Optional <OrganizationEntity> findByCif (String cif);
    Optional <OrganizationEntity> findByEmail (String email);

}
