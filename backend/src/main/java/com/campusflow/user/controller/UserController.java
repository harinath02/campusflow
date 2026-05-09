package com.campusflow.user.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.user.dto.CreateUserRequest;
import com.campusflow.user.dto.UserResponse;
import com.campusflow.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        return new ResponseEntity<>(
                ApiResponse.<UserResponse>builder()
                        .status("success")
                        .message("User created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .status("success")
                        .message("Users fetched successfully")
                        .data(users)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {

        UserResponse response = userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status("success")
                        .message("User fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Long roleId) {

        List<UserResponse> response = userService.getUsersByRole(roleId);

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .status("success")
                        .message("Users fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByDepartment(@PathVariable Long departmentId) {

        List<UserResponse> response = userService.getUsersByDepartment(departmentId);

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .status("success")
                        .message("Users fetched successfully")
                        .data(response)
                        .build()
        );
    }
}