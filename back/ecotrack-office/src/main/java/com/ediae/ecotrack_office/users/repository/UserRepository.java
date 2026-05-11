package com.ediae.ecotrack_office.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.users.entity.UserEntity;

    @Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByOrganizationId(Long organizationId);

    List<UserEntity> findByOrganizationIdAndIsActive(Long organizationId, Boolean isActive);

    boolean existsByEmail(String email);
}

