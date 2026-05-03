package com.campusflow.notification.service;

import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.notification.dto.NotificationResponse;
import com.campusflow.notification.entity.Notification;
import com.campusflow.notification.repository.NotificationRepository;
import com.campusflow.role.entity.RoleName;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void notifyUser(Long userId, String title, String message) {
        if (userId == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void notifyRole(RoleName roleName, String title, String message) {
        for (User user : userRepository.findByRoleName(roleName)) {
            notifyUser(user.getId(), title, message);
        }
    }

    public void notifyDepartment(Long departmentId, String title, String message) {
        if (departmentId == null) {
            return;
        }
        for (User user : userRepository.findByDepartmentId(departmentId)) {
            notifyUser(user.getId(), title, message);
        }
    }

    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        List<NotificationResponse> responses = new ArrayList<>();
        for (Notification notification : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            responses.add(mapToResponse(notification));
        }
        return responses;
    }

    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        return mapToResponse(notificationRepository.save(notification));
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setRead(notification.getRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
