package com.jsdc.iotpt.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class WalkPersonDao{



    public String getPersonsByIds(String ids){
        StringBuilder sql = new StringBuilder();
        sql.append(" select");
        sql.append(" u.id,");
        sql.append(" u.age,");
        sql.append(" u.createTime,");
        sql.append(" u.idCard,");
        sql.append(" u.mobile,");
        sql.append(" u.phone,");
        sql.append(" u.realName,");
        sql.append(" u.remarks,");
        sql.append(" u.sex,");
        sql.append(" u.faceId,");
        sql.append(" u.faceToken,");
        sql.append(" u.isDelete,");
        sql.append(" u.updateTime,");
        sql.append(" u.userType");
        sql.append(" FROM walk_person u");
        sql.append(" WHERE 1=1");
        sql.append(" AND u.faceId in ("+ids+")");
        return sql.toString();
    }


    public String getPersionListByDeptId(String deptId){
        StringBuilder sql = new StringBuilder();
        sql.append(" select");
        sql.append(" u.id,");
        sql.append(" u.age,");
        sql.append(" u.createTime,");
        sql.append(" u.idCard,");
        sql.append(" u.mobile,");
        sql.append(" u.phone,");
        sql.append(" u.realName,");
        sql.append(" u.remarks,");
        sql.append(" u.sex,");
        sql.append(" u.faceId,");
        sql.append(" u.faceToken,");
        sql.append(" u.isDelete,");
        sql.append(" u.updateTime,");
        sql.append(" u.userType");
        sql.append(" FROM walk_person u");
        sql.append(" WHERE 1=1");
        sql.append(" AND u.deptId ='").append(deptId).append("'");
        return sql.toString();
    }

    public String getInnerUser(Integer deptId) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select  p.id,p.idCard, p.realName,p.sex,p.phone,p.mobile,s.name as sectionOffice ,s.address,s.room ");
        sql.append(" from walk_person p,department d,sectionoffice s ");
        sql.append(" where FIND_IN_SET('6',p.usertype) and p.deptId =d.id and  p.deptId = s.deptId");
        sql.append(" and p.deptId = ").append(deptId);
        return sql.toString();
    }

    public String getIUser(Integer deptId,String realName) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select  p.id,p.idCard, p.realName,p.sex,p.phone,p.mobile,s.name as sectionOffice ,s.address,s.room ");
        sql.append(" from walk_person p,department d,sectionoffice s ");
        sql.append(" where FIND_IN_SET('6',p.usertype) and p.deptId =d.id and  p.deptId = s.deptId");
        sql.append(" and p.deptId = ").append(deptId);
        if(!"".equals(StringUtils.trimToEmpty(realName))) {
            sql.append(" and p.realName = '").append(realName).append("'");
        }
        return sql.toString();
    }
}
