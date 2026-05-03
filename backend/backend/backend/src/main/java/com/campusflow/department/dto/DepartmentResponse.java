package com.campusflow.department.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
}
