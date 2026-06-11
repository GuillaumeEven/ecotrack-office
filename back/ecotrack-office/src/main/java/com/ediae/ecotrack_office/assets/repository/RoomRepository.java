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

    // Returns the first UNAVAILABLE room on a floor, ordered by id (deterministic)
    Optional<RoomEntity> findFirstByFloor_IdAndStatusOrderByIdAsc(Long floorId, ResourceStatus status);

    List<RoomEntity> findByFloor_IdAndType(Long floorId, RoomType type);
}