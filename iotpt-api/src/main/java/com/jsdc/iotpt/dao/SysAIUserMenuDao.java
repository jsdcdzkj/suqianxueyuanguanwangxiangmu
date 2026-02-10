package com.jsdc.iotpt.dao;

import org.springframework.stereotype.Repository;

/**
 * @projectName: IOT
 * @className: SysRoleMenuDao
 * @author: wp
 * @description:
 * @date: 2023/5/10 9:42
 */
@Repository
public class SysAIUserMenuDao {

    public String getAIUserMenus(Integer userId){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" 	m.TITLE ");
        sql.append(" FROM");
        sql.append(" 	sys_ai_user_menu rm");
        sql.append(" 	LEFT JOIN SYS_AI_MENU m ON rm.MENUID = m.id ");
        sql.append(" WHERE");
        sql.append(" 	rm.userId = " + userId);
        return sql.toString();
    }
}
