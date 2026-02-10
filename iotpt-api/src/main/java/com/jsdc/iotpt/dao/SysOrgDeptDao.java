package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class SysOrgDeptDao {

    public String getList(Page page, SysOrgDept sysOrgDept, String lettersCommaSeparated){
        String sql = "SELECT\n" +
                "\t*\n" +
                "FROM\n" +
                "\tSYS_ORG_DEPT sod\n" +
                "WHERE\n" +
                "\tSOD.isDel = 0 and sod.orgId in("+lettersCommaSeparated+") " ;

        if(StringUtils.isNotEmpty(sysOrgDept.getDeptName())){
            sql += " AND sod.deptName like '%" + sysOrgDept.getDeptName() + "%'\n";
        }

        if(StringUtils.isNotEmpty(sysOrgDept.getIds())){
            sql += " and instr(sod.ids,  '"+sysOrgDept.getIds()+"')>0" ;
        }
        sql +=" order by sod.createTime desc" ;

        return sql;
    }

    public String getList2( SysOrgDept sysOrgDept, String lettersCommaSeparated){
        String sql = "SELECT\n" +
                "\t*\n" +
                "FROM\n" +
                "\tSYS_ORG_DEPT sod\n" +
                "WHERE\n" +
                "\tSOD.isDel = 0 and sod.orgId in("+lettersCommaSeparated+") " ;

        if(StringUtils.isNotEmpty(sysOrgDept.getDeptName())){
            sql += " AND sod.deptName like '%" + sysOrgDept.getDeptName() + "%'\n";
        }

        if(StringUtils.isNotEmpty(sysOrgDept.getIds())){
            sql += " and instr(sod.ids,  '"+sysOrgDept.getIds()+"')>0" ;
        }
        sql +=" order by sod.createTime desc" ;

        return sql;
    }


    public String exist(String id){
        String sql = "SELECT\n" +
                "\tcount(1)\n" +
                "    FROM\n" +
                "            sys_org_dept\n" +
                "    WHERE\n" +
                "            isdel = '0'\n" +
                "    AND INSTR (ids, "+id+") > 0" ;

        return sql;
    }


}
