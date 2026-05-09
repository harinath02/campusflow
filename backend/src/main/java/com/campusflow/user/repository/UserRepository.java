package com.campusflow.user.repository;

import com.campusflow.user.entity.User;
import com.campusflow.role.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByRoleId(Long roleId);
    List<User> findByRoleName(RoleName roleName);
    List<User> findByDepartmentId(Long departmentId);
}
