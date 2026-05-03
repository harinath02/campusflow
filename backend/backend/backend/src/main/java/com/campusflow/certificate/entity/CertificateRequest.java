package com.campusflow.certificate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "certificate_requests")
@Getter
@Setter
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private com.campusflow.request.entity.Request request;

    @Column(nullable = false)
    private String certificateType;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String deliveryMode;
}
