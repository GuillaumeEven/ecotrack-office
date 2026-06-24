package com.ediae.ecotrack_office.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ediae.ecotrack_office.users.entity.UserEntity;
import com.ediae.ecotrack_office.users.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByOrganizationId(Long organizationId);

    List<UserEntity> findByOrganizationIdAndIsActive(Long organizationId, Boolean isActive);

    boolean existsByEmail(String email);

    Page<UserEntity> findByOrganizationId(Long organizationId, Pageable pageable);

    // ─────────────────────────────────────────────
    // Búsqueda con filtros opcionales
    // Nota: u.organization.id porque organization es @ManyToOne
    // ─────────────────────────────────────────────
    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.organization.id = :organizationId
          AND (:search IS NULL OR
               LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(u.email)     LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:role IS NULL OR u.role = :role)
          AND (:isActive IS NULL OR u.isActive = :isActive)
    """)
    Page<UserEntity> findByFilters(
        @Param("organizationId") Long organizationId,
        @Param("search")         String search,
        @Param("role")           Role role,
        @Param("isActive")       Boolean isActive,
        Pageable pageable
    );

    // ─────────────────────────────────────────────
    // Contadores para las KPI cards
    // ─────────────────────────────────────────────
    long countByOrganizationId(Long organizationId);

    long countByOrganizationIdAndIsActive(Long organizationId, Boolean isActive);

    @Query("""
        SELECT COUNT(u) FROM UserEntity u
        WHERE u.organization.id = :organizationId
          AND u.createdAt >= :from
    """)
    long countNewUsersThisMonth(
        @Param("organizationId") Long organizationId,
        @Param("from")           java.time.LocalDateTime from
    );
}