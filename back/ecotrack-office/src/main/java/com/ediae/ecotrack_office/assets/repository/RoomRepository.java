package com.ediae.ecotrack_office.assets.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findByFloorId(Long floorId);

    Long countByFloorId(Long floorId);

    Long countByFloorIdAndStatus(Long floorId, ResourceStatus status);

    // Returns the first UNAVAILABLE room on a floor, ordered by id (deterministic)
    Optional<RoomEntity> findFirstByFloorIdAndStatusOrderByIdAsc(Long floorId, ResourceStatus status);
}
