package com.campusflow.nodues.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoDuesDepartmentStatusResponse {

    private Long id;
    private String department;
    private String status;
    private String remarks;
    private String clearedBy;
    private LocalDateTime clearedAt;
}
