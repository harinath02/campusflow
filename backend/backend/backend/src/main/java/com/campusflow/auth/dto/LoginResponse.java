package com.campusflow.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private Long userId;
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
