package com.campusflow.audit.controller;

import com.campusflow.audit.dto.AuditLogResponse;
import com.campusflow.audit.service.AuditService;
import com.campusflow.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs() {
        return ResponseEntity.ok(
                ApiResponse.<List<AuditLogResponse>>builder()
                        .status("success")
                        .message("Audit logs fetched successfully")
                        .data(auditService.getAuditLogs())
                        .build()
        );
    }
}
