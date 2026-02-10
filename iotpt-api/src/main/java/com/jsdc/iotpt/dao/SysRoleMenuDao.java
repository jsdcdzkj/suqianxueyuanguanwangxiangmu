package com.jsdc.iotpt.dao;

/**
 * @projectName: IOT
 * @className: SysRoleMenuDao
 * @author: wp
 * @description:
 * @date: 2023/5/10 9:42
 */
public class SysRoleMenuDao {

    public String getRoleMenus(Integer roleId){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" 	m.TITLE ");
        sql.append(" FROM");
        sql.append(" 	sys_role_menu rm");
        sql.append(" 	LEFT JOIN SYS_MENU m ON rm.MENUID = m.id ");
        sql.append(" WHERE");
        sql.append(" 	rm.ROLEID = " + roleId);
        return sql.toString();
    }
}
