package com.campusflow.user.service;

import com.campusflow.audit.service.AuditService;
import com.campusflow.department.entity.Department;
import com.campusflow.department.repository.DepartmentRepository;
import com.campusflow.exception.ResourceAlreadyExistsException;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.role.entity.Role;
import com.campusflow.role.repository.RoleRepository;
import com.campusflow.user.dto.CreateUserRequest;
import com.campusflow.user.dto.UserResponse;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       DepartmentRepository departmentRepository,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.auditService = auditService;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User email already exists");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(role);
        user.setDepartment(department);
        user.setBranch(request.getBranch());
        user.setAdmissionYear(request.getAdmissionYear());
        user.setRollNumber(request.getRollNumber());

        User saved = userRepository.save(user);
        auditService.log(saved.getId(), saved.getName(), "USER_CREATED", "USER", saved.getId(),
                "User account created for " + saved.getEmail());
        return mapToResponse(saved);
    }

    public List<UserResponse> getAllUsers() {
        List<UserResponse> responseList = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            responseList.add(mapToResponse(user));
        }
        return responseList;
    }

    public UserResponse getUserById(Long id) {
        return mapToResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public List<UserResponse> getUsersByRole(Long roleId) {
        List<UserResponse> responseList = new ArrayList<>();
        for (User user : userRepository.findByRoleId(roleId)) {
            responseList.add(mapToResponse(user));
        }
        return responseList;
    }

    public List<UserResponse> getUsersByDepartment(Long departmentId) {
        List<UserResponse> responseList = new ArrayList<>();
        for (User user : userRepository.findByDepartmentId(departmentId)) {
            responseList.add(mapToResponse(user));
        }
        return responseList;
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
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
        return response;
    }
}
