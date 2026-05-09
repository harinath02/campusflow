package com.campusflow.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;
}