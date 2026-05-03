package com.campusflow.nodues.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "no_dues_department_statuses")
@Getter
@Setter
public class NoDuesDepartmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "no_dues_request_id", nullable = false)
    private NoDuesRequest noDuesRequest;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private com.campusflow.department.entity.Department department;

    @Column(nullable = false)
    private String status; // PENDING, CLEARED, REJECTED, HOLD

    private String remarks;

    @ManyToOne
    @JoinColumn(name = "cleared_by")
    private com.campusflow.user.entity.User clearedBy;

    private LocalDateTime clearedAt;
}
