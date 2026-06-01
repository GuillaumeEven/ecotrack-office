package com.ediae.ecotrack_office.audit.repository;

import com.ediae.ecotrack_office.audit.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}