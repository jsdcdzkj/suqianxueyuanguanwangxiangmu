package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.dto.SysUserRoleDto;
import com.jsdc.iotpt.mapper.SysUserRoleMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: IOT
 * @className: SysUserRoleService
 * @author: wp
 * @description:
 * @date: 2023/5/8 16:56
 */
@Service
@Transactional
public class SysUserRoleService extends BaseService<SysUserRole> {

    @Autowired
    private SysUserRoleMapper mapper;

    public List<SysUserRoleDto> getUserRole(Integer userId){
        List<SysUserRoleDto> list = mapper.getUserRole(userId);
        return list;
    }

    public Boolean updateUserRole(SysUser sysUser){
        int id = sysUser.getId();
        baseMapper.delete(Wrappers.<SysUserRole>lambdaQuery().eq(SysUserRole::getUserId, id));
        List<Integer> roleIds = sysUser.getRoleIds();
        List<SysUserRole> userRoles = new ArrayList<>();
        if (null!=roleIds && roleIds.size() > 0) {
            for (int roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(id);
                userRole.setRoleId(roleId);
                userRole.setIsDel(G.ISDEL_NO);
                userRoles.add(userRole);
            }
        }
        if (!userRoles.isEmpty() && userRoles.size() > 0) {
            for (SysUserRole userRole : userRoles) {
                baseMapper.insert(userRole);
            }
        }
        return true;
    }


    public List<String> getRoleSymbolByUser(String userId){
        return mapper.getRoleSymbolByUser(userId);
    };
}
