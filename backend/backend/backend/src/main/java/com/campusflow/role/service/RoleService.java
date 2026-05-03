package com.campusflow.role.service;


import com.campusflow.role.dto.RoleDTO;
import com.campusflow.role.entity.Role;
import com.campusflow.role.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> getAllRoles() {

        List<RoleDTO> roleDTOList = new ArrayList<>();
        List<Role> roles = roleRepository.findAll();

        for (Role role : roles) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTOList.add(roleDTO);
        }

        return roleDTOList;
    }

}
