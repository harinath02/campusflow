package com.campusflow.department.service;

import com.campusflow.audit.service.AuditService;
import com.campusflow.department.dto.DepartmentDTO;
import com.campusflow.department.dto.DepartmentResponse;
import com.campusflow.department.entity.Department;
import com.campusflow.department.repository.DepartmentRepository;
import com.campusflow.exception.ResourceAlreadyExistsException;
import com.campusflow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {


    private final DepartmentRepository  departmentRepository;
    private final AuditService auditService;

    public List<DepartmentResponse> getAllDepartments() {

        List<Department> departments = departmentRepository.findAll();
        List<DepartmentResponse> responses = new ArrayList<>();

        for (Department dept : departments) {
            DepartmentResponse response = new DepartmentResponse();
            response.setId(dept.getId());
            response.setName(dept.getName());
            response.setCode(dept.getCode());
            response.setDescription(dept.getDescription());
            responses.add(response);
        }

        return responses;
    }

    public DepartmentResponse saveDepartment(DepartmentDTO departmentDTO) {

        if (departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new ResourceAlreadyExistsException("Department code already exists");
        }

        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setCode(departmentDTO.getCode());
        department.setDescription(departmentDTO.getDescription());

        Department saved = departmentRepository.save(department);
        auditService.log(null, "Admin API", "DEPARTMENT_CREATED", "DEPARTMENT", saved.getId(),
                "Department created: " + saved.getName());

        DepartmentResponse response = new DepartmentResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setCode(saved.getCode());
        response.setDescription(saved.getDescription());

        return response;
    }

    public DepartmentResponse getDepartmentById(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setCode(department.getCode());
        response.setDescription(department.getDescription());

        return response;
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentDTO departmentDTO) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (!department.getCode().equals(departmentDTO.getCode()) &&
            departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new ResourceAlreadyExistsException("Department code already exists");
        }

        department.setName(departmentDTO.getName());
        department.setCode(departmentDTO.getCode());
        department.setDescription(departmentDTO.getDescription());

        Department saved = departmentRepository.save(department);
        auditService.log(null, "Admin API", "DEPARTMENT_UPDATED", "DEPARTMENT", saved.getId(),
                "Department updated: " + saved.getName());

        DepartmentResponse response = new DepartmentResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setCode(saved.getCode());
        response.setDescription(saved.getDescription());

        return response;
    }

    public void deleteDepartment(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        departmentRepository.delete(department);
        auditService.log(null, "Admin API", "DEPARTMENT_DELETED", "DEPARTMENT", id,
                "Department deleted: " + department.getName());
    }
}
