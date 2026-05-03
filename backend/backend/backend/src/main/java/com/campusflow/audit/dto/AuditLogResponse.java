package com.campusflow.audit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditLogResponse {

    private Long id;
    private Long userId;
    private String actorName;
    private String action;
    private String entityType;
    private Long entityId;
    private String description;
    private LocalDateTime timestamp;
}
