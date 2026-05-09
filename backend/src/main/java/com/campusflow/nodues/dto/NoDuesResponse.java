package com.campusflow.nodues.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NoDuesResponse {

    private Long id;
    private String studentName;
    private String academicYear;
    private String semester;
    private String overallStatus;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private List<NoDuesDepartmentStatusResponse> departmentStatuses;
}
