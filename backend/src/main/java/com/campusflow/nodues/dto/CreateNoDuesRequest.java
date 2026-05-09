package com.campusflow.nodues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNoDuesRequest {

    @NotNull
    private Long studentId;

    @NotBlank
    private String academicYear;

    private String semester;
}
