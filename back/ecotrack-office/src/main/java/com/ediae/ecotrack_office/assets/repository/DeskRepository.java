package com.ediae.ecotrack_office.assets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

@Repository
public interface DeskRepository extends JpaRepository<DeskEntity, Long> {
    List<DeskEntity> findByRoom_Id(Long roomId);

    List<DeskEntity> findByRoom_IdAndIsActiveTrue(Long roomId);

    long countByRoom_IdAndStatus(Long roomId, ResourceStatus status);

    Long countByRoom_Id(Long roomId);

}
