package com.ediae.ecotrack_office.assets.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.assets.entity.RoomEntity;
import com.ediae.ecotrack_office.assets.enums.ResourceStatus;
import com.ediae.ecotrack_office.assets.enums.RoomType;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findByFloor_Id(Long floorId);

    Long countByFloor_Id(Long floorId);

    Long countByFloor_IdAndStatus(Long floorId, ResourceStatus status);

    // Returns the first UNAVAILABLE room on a floor of a given type, ordered by id (deterministic)
    Optional<RoomEntity> findFirstByFloor_IdAndTypeAndStatusOrderByIdAsc(Long floorId, RoomType type, ResourceStatus status);

    // Returns all AVAILABLE rooms of a given type across the organization (via floor FK chain)
    List<RoomEntity> findByFloor_Organization_IdAndTypeAndStatus(Long organizationId, RoomType type, ResourceStatus status);
}
