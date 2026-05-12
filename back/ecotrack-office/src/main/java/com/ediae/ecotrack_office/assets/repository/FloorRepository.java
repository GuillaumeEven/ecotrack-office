package com.ediae.ecotrack_office.assets.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.assets.entity.FloorEntity;

@Repository
public interface FloorRepository {

    Optional<FloorEntity> findById(Long id);

    List<Optional<FloorEntity>> findByOrganizationId(Long organizationId);

    FloorEntity save(FloorEntity floor);

    void deleteById(Long id);

}
