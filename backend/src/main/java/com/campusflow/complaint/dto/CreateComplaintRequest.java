package com.campusflow.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComplaintRequest {

    @NotNull
    private Long requesterId;

    @NotBlank
    private String category;

    @NotBlank
    private String location;

    @NotBlank
    private String severity;

    @NotNull
    private Long assignedDepartmentId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
