package com.ediae.ecotrack_office.audit.service;

import com.ediae.ecotrack_office.audit.entity.AuditLogEntity;
import com.ediae.ecotrack_office.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String eventType, String entityType, Long entityId, Long actorId) {
        AuditLogEntity entry = new AuditLogEntity();
        entry.setEventType(eventType);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setActorId(actorId);
        entry.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(entry);
    }
}