package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.new_alarm.FireAlarmInfo;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.FireAlarmInfoVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class FireAlarmInfoDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param vo
     * @return
     */
    public String getEntityList(Page page, FireAlarmInfoVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT a.*,wi.deviceName,sba.AREANAME,wi.alarmContentStr,wi.alarmTime,floor.floorName,build.buildName FROM FIRE_ALARM_INFO a  ");
        sql.append(" LEFT JOIN ALARM_RECORDS wi ON a.alarmId = wi.ID ");
        sql.append(" LEFT JOIN SYS_BUILD_AREA sba ON wi.areaId = SBA.ID ");
//        sql.append(" LEFT JOIN DEVICE_COLLECT TEMP ON a.deviceId=TEMP.id ");
        sql.append(" LEFT JOIN SYS_BUILD build ON wi.BUILDID = build.id  ");
        sql.append(" LEFT JOIN SYS_BUILD_FLOOR floor ON wi.FLOORID = floor.ID   ");
        sql.append(" WHERE 1=1  ");
        sql.append(" and a.isdel=0");
        if (Strings.isNotEmpty(vo.getStartTime())) {
            sql.append(" AND wi.alarmTime >= '" + vo.getStartTime()+" 00:00:00" + "'");
        }
        if (Strings.isNotEmpty(vo.getEndTime())) {
            sql.append(" AND  wi.alarmTime <= '" + vo.getEndTime()+" 23:59:59" + "'");
        }
        if (Strings.isNotEmpty(vo.getStartHanlder())) {
            sql.append("\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) >= '").append(vo.getStartHanlder()).append("' \n");
        }
        if (Strings.isNotEmpty(vo.getEndHanlder() )) {
            sql.append("\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) <= '").append(vo.getEndHanlder()).append("' \n");
        }
        if (Strings.isNotEmpty(vo.getWarnSource())) {
            sql.append(" AND wi.deviceSource = '" + vo.getWarnSource() + "'");
        }
        if (Strings.isNotEmpty(vo.getAreaName())) {
            sql.append(" AND SBA.AREANAME LIKE '%" + vo.getAreaName() + "%'");
        }
        if (Strings.isNotEmpty( vo.getArea())) {
            sql.append(" AND wi.areaId=" + vo.getArea());
        }
        if (Strings.isNotEmpty(vo.getFloor())) {
            sql.append(" AND wi.floorId=" + vo.getFloor());
        }
        if (null != vo.getId()) {
            sql.append(" AND wi.id=" + vo.getId());
        }
        if (StringUtils.isNotEmpty(vo.getDeviceName())) {
            sql.append(" AND wi.deviceName like '%").append(vo.getDeviceName()).append("%'");
        }

        sql.append("   order by  wi.ALARMTIME desc ");

        return sql.toString();
    }


    public String export( FireAlarmInfoVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT a.*,wi.deviceName,sba.AREANAME,wi.alarmContentStr,wi.alarmTime,floor.floorName,build.buildName FROM FIRE_ALARM_INFO a  ");
        sql.append(" LEFT JOIN ALARM_RECORDS wi ON a.alarmId = wi.ID ");
        sql.append(" LEFT JOIN SYS_BUILD_AREA sba ON WI.areaId = SBA.ID ");
//        sql.append(" LEFT JOIN DEVICE_COLLECT TEMP ON WI.DEVICECOLLECTID=TEMP.id ");
        sql.append(" LEFT JOIN SYS_BUILD build ON WI.BUILDID = build.id  ");
        sql.append(" LEFT JOIN SYS_BUILD_FLOOR floor ON WI.FLOORID = floor.ID   ");
        sql.append(" WHERE 1=1  ");
        sql.append(" and a.isdel=0");
        if (Strings.isNotEmpty(vo.getStartTime())) {
            sql.append(" AND wi.alarmTime >= '" + vo.getStartTime()+" 00:00:00" + "'");
        }
        if (Strings.isNotEmpty(vo.getEndTime())) {
            sql.append(" AND  wi.alarmTime <= '" + vo.getEndTime()+" 23:59:59" + "'");
        }
        if (Strings.isNotEmpty(vo.getStartHanlder())) {
            sql.append("\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) >= '").append(vo.getStartHanlder()).append("' \n");
        }
        if (Strings.isNotEmpty(vo.getEndHanlder() )) {
            sql.append("\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) <= '").append(vo.getEndHanlder()).append("' \n");
        }
        if (Strings.isNotEmpty(vo.getWarnSource())) {
            sql.append(" AND WI.deviceSource = '" + vo.getWarnSource() + "'");
        }
        if (Strings.isNotEmpty(vo.getAreaName())) {
            sql.append(" AND SBA.AREANAME LIKE '%" + vo.getAreaName() + "%'");
        }
        if (Strings.isNotEmpty( vo.getArea())) {
            sql.append(" AND wi.areaId=" + vo.getArea());
        }
        if (Strings.isNotEmpty(vo.getFloor())) {
            sql.append(" AND wi.floorId=" + vo.getFloor());
        }
        if (null != vo.getId()) {
            sql.append(" AND wi.id=" + vo.getId());
        }
        if (StringUtils.isNotEmpty(vo.getDeviceName())) {
            sql.append(" AND wi.deviceName like '%").append(vo.getDeviceName()).append("%'");
        }

        sql.append(" order by  wi.ALARMTIME desc ");

        return sql.toString();
    }


    public String lineM(FireAlarmInfoVo vo){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "\t\tTO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) day,\n" +
                "\tCOUNT( 1 ) num\n" +
                "FROM\n" +
                "\tFIRE_ALARM_INFO a \n" +
                "WHERE\n" +
                "\ta.ISDEL = 0 \n" +
                "\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) >= '"+vo.getStartTime()+"' \n" +
                "\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' ) <= '"+vo.getEndTime()+"' \n" +
                "GROUP BY\n" +
                "\tTO_CHAR( a.HANLDERTIME, 'yyyy-MM-dd' )");
        return sql.toString();
    }

    public String lineY(FireAlarmInfoVo vo){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "\t\tTO_CHAR( a.HANLDERTIME, 'yyyy-MM' ) day,\n" +
                "\tCOUNT( 1 ) num\n" +
                "FROM\n" +
                "\tFIRE_ALARM_INFO a \n" +
                "WHERE\n" +
                "\ta.ISDEL = 0 \n" +
                "\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM' ) >= '"+vo.getStartTime()+"' \n" +
                "\tAND TO_CHAR( a.HANLDERTIME, 'yyyy-MM' ) <= '"+vo.getEndTime()+"' \n" +
                "GROUP BY\n" +
                "\tTO_CHAR( a.HANLDERTIME, 'yyyy-MM' )");
        return sql.toString();
    }


    public String column(FireAlarmInfoVo vo,Integer type,Integer isAll){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n" +
                "\tCOUNT(1) \n" +
                "FROM\n" +
                "\tALARM_RECORDS a \n" +
                "WHERE\n" +
                "\ta.alarmCategory = 6 \n" );
        if (type==0){
            if (Strings.isNotEmpty(vo.getStartTime())) {
                sql.append("\tAND  a.ALARMTIME LIKE  '").append(vo.getStartTime()).append("%'");
            }
        }else {
            if (Strings.isNotEmpty(vo.getStartHanlder() )) {
                sql.append("\tAND  a.ALARMTIME LIKE '").append(vo.getStartTime()).append("%' \n");
            }
        }
        if (isAll==1) {
            sql.append("\tAND a.HANDLESTATUS= '0' \n");
        }
        return sql.toString();
    }


//    public String columnF(FireAlarmInfoVo vo,Integer type){
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT\n" +
//                "\tCOUNT(1) \n" +
//                "FROM\n" +
//                "\tFIRE_ALARM_INFO a \n" +
//                "WHERE\n" +
//                "\ta.ISDEL=0\n" );
//        if (type==0){
//            if (Strings.isNotEmpty(vo.getStartTime())) {
//                sql.append(" AND TO_CHAR( a.hanlderTime, 'yyyy-MM' ) = '").append(vo.getStartHanlder()).append("'");
//            }
//        }else { if (vo.getStartHanlder() != null) {
//            sql.append("\tAND TO_CHAR( a.hanlderTime, 'yyyy' ) = '").append(vo.getStartHanlder()).append("' \n");
//        }}
//        return sql.toString();
//    }

}
