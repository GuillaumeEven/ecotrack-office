package com.ediae.ecotrack_office.assets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.assets.entity.DeskEntity;

@Repository
public interface DeskRepository extends JpaRepository<DeskEntity, Long> {
    List<DeskEntity> findByRoomId(Long roomId);
}
