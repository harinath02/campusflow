package com.campusflow.department.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.department.dto.DepartmentDTO;
import com.campusflow.department.dto.DepartmentResponse;
import com.campusflow.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {

        List<DepartmentResponse> departments = departmentService.getAllDepartments();

        return ResponseEntity.ok(
                ApiResponse.<List<DepartmentResponse>>builder()
                        .status("success")
                        .message("Departments fetched successfully")
                        .data(departments)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {

        DepartmentResponse response = departmentService.saveDepartment(departmentDTO);

        return new ResponseEntity<>(
                ApiResponse.<DepartmentResponse>builder()
                        .status("success")
                        .message("Department created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {

        DepartmentResponse response = departmentService.getDepartmentById(id);

        return ResponseEntity.ok(
                ApiResponse.<DepartmentResponse>builder()
                        .status("success")
                        .message("Department fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDTO departmentDTO) {

        DepartmentResponse response = departmentService.updateDepartment(id, departmentDTO);

        return ResponseEntity.ok(
                ApiResponse.<DepartmentResponse>builder()
                        .status("success")
                        .message("Department updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Department deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
