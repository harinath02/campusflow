package com.campusflow.audit.service;

import com.campusflow.audit.dto.AuditLogResponse;
import com.campusflow.audit.entity.AuditLog;
import com.campusflow.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long userId, String actorName, String action, String entityType, Long entityId, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setActorName(actorName == null || actorName.isBlank() ? "System" : actorName);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAuditLogs() {
        List<AuditLogResponse> responses = new ArrayList<>();
        for (AuditLog auditLog : auditLogRepository.findAllByOrderByTimestampDesc()) {
            responses.add(mapToResponse(auditLog));
        }
        return responses;
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(auditLog.getId());
        response.setUserId(auditLog.getUserId());
        response.setActorName(auditLog.getActorName());
        response.setAction(auditLog.getAction());
        response.setEntityType(auditLog.getEntityType());
        response.setEntityId(auditLog.getEntityId());
        response.setDescription(auditLog.getDescription());
        response.setTimestamp(auditLog.getTimestamp());
        return response;
    }
}
