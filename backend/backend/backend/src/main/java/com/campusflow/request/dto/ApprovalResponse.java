package com.campusflow.request.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprovalResponse {

    private Long id;
    private Long requestId;
    private Long departmentId;
    private String department;
    private String status;
    private String remarks;
    private LocalDateTime actionAt;
}
