package com.campusflow.nodues.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "no_dues_requests")
@Getter
@Setter
public class NoDuesRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private com.campusflow.user.entity.User student;

    @Column(nullable = false)
    private String academicYear;

    private String semester;

    @Column(nullable = false)
    private String overallStatus; // PENDING, COMPLETED, REJECTED, HOLD

    @Column(nullable = false)
    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;
}
