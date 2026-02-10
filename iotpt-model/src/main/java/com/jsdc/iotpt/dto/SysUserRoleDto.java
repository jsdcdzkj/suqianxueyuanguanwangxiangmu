package com.jsdc.iotpt.dto;

import lombok.Data;

/**
 * @projectName: IOT
 * @className: SysUserRoleDto
 * @author: wp
 * @description:
 * @date: 2023/5/10 16:09
 */
@Data
public class SysUserRoleDto {
    private Integer id;

    private String roleName;

    private String roleFlag;
}
