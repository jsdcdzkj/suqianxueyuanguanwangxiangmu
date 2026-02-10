package com.jsdc.iotpt.dao;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

@Repository
public class WalkActionLocusDao {


    public String getActionsById(String faceId, String time, Integer floor) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" a.id,");
        sql.append(" a.faceId,");
        sql.append(" a.label,");
        sql.append(" a.equipmentId,");
        sql.append(" a.areaId,");
        sql.append(" a.deptId,");
        sql.append(" a.longitude,");
        sql.append(" a.latitude,");
        sql.append(" a.filePath,");
        sql.append(" a.createTime,");
        sql.append(" a.updateTime,");
        sql.append(" e.areaName");
        sql.append(" FROM walk_action_locus a");
        sql.append(" left join AREA e on e.id=a.areaId");
        sql.append(" WHERE 1=1");
        sql.append(" AND a.faceId = '" + faceId + "'");
        sql.append(" AND a.createTime >= '" + time + " 00:00:00'");
        sql.append(" AND a.createTime <= '" + time + " 23:59:59'");
        sql.append(" AND a.isDelete = 0");
        if (null != floor) {
            sql.append(" AND e.floor = ").append(floor);
        }
        sql.append(" ORDER BY a.createTime asc");

        return sql.toString();
    }

    public String getActionsByTime(String faceId, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" a.id,");
        sql.append(" a.faceId,");
        sql.append(" a.label,");
        sql.append(" a.equipmentId,");
        sql.append(" a.areaId,");
        sql.append(" a.deptId,");
        sql.append(" a.longitude,");
        sql.append(" a.latitude,");
        sql.append(" a.filePath,");
        sql.append(" a.createTime,");
        sql.append(" a.updateTime,");
        sql.append(" e.areaName");
        sql.append(" FROM walk_action_locus a");
        sql.append(" left join AREA e on e.id=a.areaId");
        sql.append(" WHERE 1=1");
        sql.append(" AND a.faceId = '" + faceId + "'");
        sql.append(" AND a.createTime >= '" + startTime + " 00:00:00'");
        sql.append(" AND a.createTime <= '" + endTime + " 23:59:59'");
        sql.append(" AND a.isDelete = 0");
        sql.append(" ORDER BY a.createTime asc");

        return sql.toString();
    }

    public String signInAndOut(String realName, String createTime, Integer isFlag, Integer deptId, String turnstileType) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("a.id, ");
        sql.append("a.createTime, ");
        sql.append("a.faceId, ");
        sql.append("a. WEEK, ");
        sql.append("a.timeDivision, ");
        sql.append("a.turnstileType, ");
        sql.append("a.isFlag, ");
        sql.append("p.realName, ");
        sql.append("p.idCard, ");
        sql.append("p.phone, ");
        sql.append("p.mobile, ");
        sql.append("p.age, ");
        sql.append("p.sex, ");
        sql.append("p.userType, ");
        sql.append("u.filePath, ");
        sql.append("la.labelName, ");
        sql.append("area.areaName ");
        sql.append("FROM ");
        sql.append("walk_action_locus a ");
        sql.append("LEFT JOIN person p ON a.faceId = p.faceId ");
        sql.append("LEFT JOIN userfile u ON a.faceId = u.faceId ");
        sql.append("LEFT JOIN label la ON p.userType=la.labelCode ");
        sql.append("LEFT JOIN area  ON a.areaId=area.id  ");
        sql.append("WHERE ");
        sql.append("to_days(a.createTime) = to_days('" + createTime + "') ");
        sql.append("AND turnstileType in  " + turnstileType);
        sql.append(" AND warnFlag=0 ");
        if (Strings.isNotEmpty(realName)) {
            sql.append(" AND p.realName like '%" + realName + "%'");
        }
        if (isFlag != null) {
            sql.append(" AND a.isFlag=" + isFlag);
        }

        if (deptId != null) {
            sql.append(" AND a.deptId=" + deptId);
        }
        sql.append(" ORDER BY a.createTime  ");


        System.out.println(sql.toString());

        return sql.toString();
    }

    public String signInAndOut2(String createTime, Integer isFlag, Integer deptId, String turnstileType, Integer floor) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("a.id, ");
        sql.append("a.createTime, ");
        sql.append("a.faceId, ");
        sql.append("a. WEEK, ");
        sql.append("a.timeDivision, ");
        sql.append("a.turnstileType, ");
        sql.append("a.isFlag, ");
        sql.append("a.latitude, ");
        sql.append("a.longitude, ");
        sql.append("p.realName, ");
        sql.append("p.idCard, ");
        sql.append("p.phone, ");
        sql.append("p.mobile, ");
        sql.append("p.age, ");
        sql.append("p.sex, ");
        sql.append("p.userType, ");
        sql.append("u.filePath, ");
        sql.append("la.labelName, ");
        sql.append("area.areaName ");
        sql.append("FROM ");
        sql.append("walk_action_locus a ");
        sql.append("LEFT JOIN person p ON a.faceId = p.faceId ");
        sql.append("LEFT JOIN userfile u ON a.faceId = u.faceId ");
        sql.append("LEFT JOIN label la ON p.userType=la.labelCode ");
        sql.append("LEFT JOIN area  ON a.areaId=area.id  ");
        sql.append("WHERE ");
        sql.append("to_days(a.createTime) = to_days('" + createTime + "') ");
        sql.append("AND turnstileType in  " + turnstileType);
        sql.append(" AND warnFlag=0 ");
        if (deptId != null) {
            sql.append(" AND a.deptId=" + deptId);
        }
        if (null != floor) {
            sql.append(" AND area.floor = " + floor);
        }
        sql.append(" ORDER BY a.createTime  ");

        return sql.toString();
    }


    public String selectToDayById(Integer id) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("ac.* ");
        sql.append("FROM ");
        sql.append("walk_action_locus ac ");
        sql.append("LEFT JOIN ( ");
        sql.append("SELECT ");
        sql.append("faceId, ");
        sql.append("createTime ");
        sql.append("FROM ");
        sql.append("walk_action_locus ");
        sql.append("WHERE ");
        sql.append("id =  " + id);
        sql.append(" ) a ON a.faceId = ac.faceId ");
        sql.append("WHERE ");
        sql.append("TO_DAYS(a.createTime) = TO_DAYS(ac.createTime) ");
        sql.append("AND ac.warnFlag = 0 ");
        sql.append("AND ac.turnstileType IN (0, 1,2) ");


        return sql.toString();
    }
}
