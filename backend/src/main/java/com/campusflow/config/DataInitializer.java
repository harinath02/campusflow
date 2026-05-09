package com.campusflow.config;

import com.campusflow.department.entity.Department;
import com.campusflow.department.repository.DepartmentRepository;
import com.campusflow.requesttype.dto.CreateRequestTypeRequest;
import com.campusflow.requesttype.repository.RequestTypeRepository;
import com.campusflow.requesttype.service.RequestTypeService;
import com.campusflow.role.entity.Role;
import com.campusflow.role.entity.RoleName;
import com.campusflow.role.repository.RoleRepository;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final RequestTypeService requestTypeService;
    private final UserRepository userRepository;

    public DataInitializer(RoleRepository roleRepository, DepartmentRepository departmentRepository,
                           RequestTypeRepository requestTypeRepository, RequestTypeService requestTypeService,
                           UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.requestTypeService = requestTypeService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedDepartments();
        seedRequestTypes();
        seedUsers();
    }

    private void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }

    private void seedDepartments() {
        createDepartmentIfMissing("Administration", "ADM", "Campus administration and approvals");
        createDepartmentIfMissing("Academics", "ACD", "Academic request review and student support");
        createDepartmentIfMissing("Finance", "FIN", "Fee, payment, and finance approvals");
        createDepartmentIfMissing("Library", "LIB", "Library clearance and records");
    }

    private void createDepartmentIfMissing(String name, String code, String description) {
        boolean exists = departmentRepository.findAll().stream()
                .anyMatch(dept -> code.equalsIgnoreCase(dept.getCode()) || name.equalsIgnoreCase(dept.getName()));
        if (!exists) {
            Department department = new Department();
            department.setName(name);
            department.setCode(code);
            department.setDescription(description);
            departmentRepository.save(department);
        }
    }

    private void seedRequestTypes() {
        if (requestTypeRepository.count() == 0) {
            createRequestType("Certificate Request", "CERT", "Certificate, bonafide, transcript, and academic document approvals");
            createRequestType("Complaint", "COMP", "Campus issue, facility, or service complaint approvals");
            createRequestType("No Dues", "NODUES", "Department clearance workflow before final approval");
        }
    }

    private void createRequestType(String name, String code, String description) {
        CreateRequestTypeRequest request = new CreateRequestTypeRequest();
        request.setName(name);
        request.setCode(code);
        request.setDescription(description);
        requestTypeService.createRequestType(request);
    }

    private void seedUsers() {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
        Role officerRole = roleRepository.findByName(RoleName.OFFICER).orElseThrow();
        Role studentRole = roleRepository.findByName(RoleName.STUDENT).orElseThrow();

        Department library = departmentRepository.findAll().stream()
                .filter(department -> "LIB".equalsIgnoreCase(department.getCode()))
                .findFirst()
                .orElse(null);

        createUserIfMissing("Admin User", "admin@campusflow.test", "admin123", adminRole, null, null, null, null);
        createUserIfMissing("Library Officer", "library@campusflow.test", "officer123", officerRole, library, null, null, null);
        createUserIfMissing("Aarav Sharma", "aarav@campusflow.test", "student123", studentRole, null, "CSE", 2022, "CSE-102");
    }

    private void createUserIfMissing(String name, String email, String password, Role role, Department department,
                                     String branch, Integer admissionYear, String rollNumber) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setDepartment(department);
        user.setBranch(branch);
        user.setAdmissionYear(admissionYear);
        user.setRollNumber(rollNumber);
        userRepository.save(user);
    }
}
