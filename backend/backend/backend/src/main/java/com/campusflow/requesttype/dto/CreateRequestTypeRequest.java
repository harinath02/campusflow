package com.campusflow.requesttype.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestTypeRequest {

    @NotBlank(message = "Request type name is required")
    private String name;

    @NotBlank(message = "Request type code is required")
    private String code;

    private String description;
}