package com.campusflow.certificate.repository;

import com.campusflow.certificate.entity.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {
}
