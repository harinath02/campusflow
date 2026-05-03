package com.campusflow.complaint.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComplaintResponse {

    private Long id;
    private Long requestId;
    private String requestNumber;
    private String category;
    private String location;
    private String severity;
    private String assignedDepartment;
    private String status;
    private String resolutionNote;
    private LocalDateTime resolvedAt;
}
