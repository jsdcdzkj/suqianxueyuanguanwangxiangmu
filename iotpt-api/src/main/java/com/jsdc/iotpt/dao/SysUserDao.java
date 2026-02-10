package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.util.StringUtils; /**
 * @projectName: IOT
 * @className: SysUserDao
 * @author: wp
 * @description:
 * @date: 2023/5/8 16:12
 */
public class SysUserDao {

    public String getUsersByRole(String administration){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT SU.\"ID\",SU.PHONE FROM SYS_USER su ");
        sql.append("LEFT JOIN SYS_USER_ROLE sur ON SU.\"ID\"=SUR.USERID ");
        sql.append("LEFT JOIN SYS_ROLE sr ON sur.roleId=sr.id  ");
        sql.append("WHERE su.isDel=0 and sr.roleflag='"+administration+"' ");
        return sql.toString();
    }

    public String selectIsDirector(Integer userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT\tsu.* FROM\tSYS_USER su LEFT JOIN SYS_USER_ROLE sur ON SU.\"ID\" = SUR.USERID LEFT JOIN SYS_ROLE sr ON sr.\"ID\" = sur.ROLEID WHERE sr.ROLENAME = '行政管理' AND SU.\"ID\" = " + userId);
        return builder.toString();
    }

    /**
     * 去重后的手机号用户
     *
     * @return
     */
    public String selectUserPhone() {
        StringBuilder builder = new StringBuilder();
        builder.append("select DEPTID,REALNAME,PHONE from sys_user where ISDEL=0 and phone is not null ");
        builder.append(" GROUP BY DEPTID,REALNAME,PHONE ORDER BY DEPTID,REALNAME ");
        return builder.toString();
    }


    public String getApproveUser(String componyId,String roleFlag) {
        String sql ="SELECT\n" +
                "  * \n" +
                "FROM\n" +
                "  SYS_USER su\n" +
                "  LEFT JOIN SYS_USER_ROLE sur ON su.id = sur.USERID\n" +
                "  LEFT JOIN SYS_ROLE sr ON sur.RoleId = sr.id\n" +
                "  where sr.roleFlag = '"+roleFlag+"' and sr.ISDEL='0' and su.ISDEL='0' " ;
        if(StringUtils.isNotEmpty(componyId)){
            sql += " and su.unitId='"+componyId+"'";
        }
        return sql;
    }

}
