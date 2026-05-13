package com.ediae.ecotrack_office.assets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.assets.entity.FloorEntity;

@Repository
public interface FloorRepository extends JpaRepository<FloorEntity, Long> {
    List<FloorEntity> findByOrganizationId(Long organizationId);
}
