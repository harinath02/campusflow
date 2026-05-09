package com.campusflow.nodues.repository;

import com.campusflow.nodues.entity.NoDuesDepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoDuesDepartmentStatusRepository extends JpaRepository<NoDuesDepartmentStatus, Long> {

    List<NoDuesDepartmentStatus> findByNoDuesRequestId(Long noDuesRequestId);
    Optional<NoDuesDepartmentStatus> findByNoDuesRequestIdAndDepartmentId(Long noDuesRequestId, Long departmentId);
    boolean existsByNoDuesRequestIdAndStatusNot(Long noDuesRequestId, String status);
}
