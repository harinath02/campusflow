package com.campusflow.department.repository;

import com.campusflow.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByCode(String code);
}
