package com.campusflow.requesttype.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestTypeResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean active;
}