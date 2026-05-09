package com.campusflow.request.repository;

import com.campusflow.request.entity.Request;
import com.campusflow.request.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long requesterId);
    List<Request> findByStatus(RequestStatus status);
    List<Request> findByRequestTypeId(Long requestTypeId);
}