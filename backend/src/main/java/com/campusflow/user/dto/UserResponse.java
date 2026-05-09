package com.campusflow.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Long departmentId;
    private String department;
    private String branch;
    private Integer admissionYear;
    private String rollNumber;
}