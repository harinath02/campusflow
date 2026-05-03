package com.campusflow.nodues.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectNoDuesDepartmentRequest {

    @NotNull
    private Long departmentId;

    @NotNull
    private Long officerId;

    private String remarks;
}
