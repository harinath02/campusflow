package com.campusflow.request.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestResponse {

    private Long id;
    private String requestNumber;
    private Long requestTypeId;
    private String requestType;
    private Long requesterId;
    private String requesterName;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime expectedCompletionTime;
    private LocalDateTime actualCompletionTime;
    private Boolean delayed;
}
