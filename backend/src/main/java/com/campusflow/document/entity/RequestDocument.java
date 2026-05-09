package com.campusflow.document.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_documents")
@Getter
@Setter
public class RequestDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private com.campusflow.request.entity.Request request;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private com.campusflow.user.entity.User uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}
