package com.jsdc.iotpt.dao;


/**
 * @projectName: IOT
 * @className: SysRoleDao
 * @author: wp
 * @description:
 * @date: 2023/5/8 16:50
 */
public class SysRoleDao {

    //获取角色标识
    public String selectByRoleFlag(Integer userId) {
        String sql = "select e.roleFlag from sys_user u LEFT JOIN SYS_USER_ROLE ur on u.\"ID\"=ur.USERID " +
                " LEFT JOIN SYS_ROLE e on ur.ROLEID = e.id where u.id='" + userId + "'";
        return sql;
    }
}
