package com.campusflow.auth.controller;

import com.campusflow.auth.dto.LoginResponse;
import com.campusflow.auth.dto.LoginRequest;
import com.campusflow.auth.service.JwtService;
import com.campusflow.common.ApiResponse;
import com.campusflow.exception.BadRequestException;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No user found with this email"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtService.generateToken(user));
        response.setUserId(user.getId());
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName().name());
        if (user.getDepartment() != null) {
            response.setDepartmentId(user.getDepartment().getId());
            response.setDepartment(user.getDepartment().getName());
        }
        response.setBranch(user.getBranch());
        response.setAdmissionYear(user.getAdmissionYear());
        response.setRollNumber(user.getRollNumber());

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .status("success")
                .message("Login successful")
                .data(response)
                .build());
    }
}
