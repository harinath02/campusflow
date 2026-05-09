package com.campusflow.request.repository;

import com.campusflow.request.entity.RequestApproval;
import com.campusflow.request.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestApprovalRepository extends JpaRepository<RequestApproval, Long> {

    List<RequestApproval> findByRequestId(Long requestId);
    List<RequestApproval> findByDepartmentId(Long departmentId);
    List<RequestApproval> findByDepartmentIdAndStatus(Long departmentId, RequestStatus status);
    Optional<RequestApproval> findByRequestIdAndDepartmentId(Long requestId, Long departmentId);
    boolean existsByRequestIdAndStatusNot(Long requestId, RequestStatus status);
}
