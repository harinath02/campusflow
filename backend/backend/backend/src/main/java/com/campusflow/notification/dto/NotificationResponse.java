package com.campusflow.notification.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Boolean read;
    private LocalDateTime createdAt;
}
