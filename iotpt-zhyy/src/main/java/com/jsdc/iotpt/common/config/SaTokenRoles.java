package com.jsdc.iotpt.common.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaTokenRoles implements StpInterface {

//    @Autowired
//    private SysUserRoleService sysUserRoleService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }
    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
       // List<String> list = sysUserRoleService.getRoleSymbolByUser(loginId+"");
        return null;
    }
}
