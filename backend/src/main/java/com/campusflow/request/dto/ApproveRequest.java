package com.campusflow.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveRequest {

    @NotNull
    private Long requestId;
    @NotNull
    private Long departmentId;
    private Long actorUserId;
    private String remarks;
}
