package com.campusflow.role.dto;

import com.campusflow.role.entity.RoleName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {

    private long id;
    private RoleName name;
}