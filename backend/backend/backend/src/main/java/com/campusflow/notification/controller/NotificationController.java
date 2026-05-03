package com.campusflow.notification.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.notification.dto.NotificationResponse;
import com.campusflow.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .status("success")
                        .message("Notifications fetched successfully")
                        .data(notificationService.getNotificationsByUser(userId))
                        .build()
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<NotificationResponse>builder()
                        .status("success")
                        .message("Notification marked as read")
                        .data(notificationService.markAsRead(id))
                        .build()
        );
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Notifications marked as read")
                        .data(null)
                        .build()
        );
    }
}
