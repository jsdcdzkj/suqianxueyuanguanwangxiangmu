package com.jsdc.iotpt.dao;

/**
 * @projectName: IOT
 * @className: SysUserRoleDao
 * @author: wp
 * @description:
 * @date: 2023/5/8 16:56
 */
public class SysUserRoleDao {

    public String getUserRole(Integer userId){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" 	r.ROLENAME roleName, r.ROLEFLAG roleFlag");
        sql.append(" FROM");
        sql.append(" 	sys_user_role ur");
        sql.append(" 	LEFT JOIN SYS_ROLE r ON ur.ROLEID = r.ID ");
        sql.append(" WHERE");
        sql.append(" 	ur.USERID = " + userId);
        return sql.toString();
    }

    public String getRoleSymbolByUser(String userId){
        String sql = "SELECT\n" +
                "\tb.roleFlag\n" +
                "FROM\n" +
                "\tSYS_USER_ROLE A\n" +
                "LEFT JOIN SYS_ROLE b ON A .RoleId = b. ID\n" +
                "WHERE\n" +
                "\t A.userId = '"+userId+"'\n" +
                "AND b.isDel = '0' and b.roleFlag is not null";
        return sql;
    }
}
