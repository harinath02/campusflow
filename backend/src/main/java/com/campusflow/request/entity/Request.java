package com.campusflow.request.entity;

import com.campusflow.requesttype.entity.RequestType;
import com.campusflow.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String requestNumber;

    @ManyToOne
    @JoinColumn(name = "request_type_id", nullable = false)
    private RequestType requestType;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    private String priority;

    private LocalDateTime createdAt;

    private LocalDateTime submittedAt;

    private LocalDateTime expectedCompletionTime;

    private LocalDateTime actualCompletionTime;

    private Boolean delayed = false;
}
