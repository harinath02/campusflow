package com.campusflow.requesttype.repository;

import com.campusflow.requesttype.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {

    boolean existsByCode(String code);
}