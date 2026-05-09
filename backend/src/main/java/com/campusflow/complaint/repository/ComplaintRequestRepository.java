package com.campusflow.complaint.repository;

import com.campusflow.complaint.entity.ComplaintRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRequestRepository extends JpaRepository<ComplaintRequest, Long> {
}
