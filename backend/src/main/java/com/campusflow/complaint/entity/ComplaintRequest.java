package com.campusflow.complaint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_requests")
@Getter
@Setter
public class ComplaintRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private com.campusflow.request.entity.Request request;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String severity;

    @ManyToOne
    @JoinColumn(name = "assigned_department_id")
    private com.campusflow.department.entity.Department assignedDepartment;

    private String resolutionNote;

    private LocalDateTime resolvedAt;
}
