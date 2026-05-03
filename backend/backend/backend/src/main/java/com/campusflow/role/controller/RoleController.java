package com.campusflow.role.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.role.dto.RoleDTO;
import com.campusflow.role.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        return ResponseEntity.ok(
                ApiResponse.<List<RoleDTO>>builder()
                        .status("success")
                        .message("Roles fetched successfully")
                        .data(roleService.getAllRoles())
                        .build()
        );
    }

    @GetMapping("/getRoles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRolesLegacy() {
        return getAllRoles();
    }
}
