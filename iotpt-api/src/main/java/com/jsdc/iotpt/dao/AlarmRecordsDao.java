package com.jsdc.iotpt.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.AlarmRecordsVo;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class AlarmRecordsDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, AlarmRecords bean) {
        String sql = " SELECT *  "+
                " FROM ALARM_RECORDS  "+
                " WHERE "+ 
                " 1=1 ";

        return sql;
    }


    public String getSubscript(AlarmRecordsVo vo){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) num,a.ALARMCATEGORY FROM ALARM_RECORDS a  WHERE 1=1 AND a.ISDEL=0 " );
        if (vo.getAlarmType()!=null){
            sql.append(" AND a.ALARMTYPE ='").append(vo.getAlarmType()).append("' ");
        }
        if (vo.getAreaId()!=null){
            sql.append(" AND a.AREAID ='").append(vo.getAreaId()).append("' ");
        }
        if (vo.getFloorId()!=null){
            sql.append(" AND a.FLOORID ='").append(vo.getFloorId()).append("' ");
        }
        if (vo.getAlarmCategory()!=null){
            sql.append(" AND a.ALARMCATEGORY ='").append(vo.getAlarmCategory()).append("' ");
        }
        if (vo.getDeviceSource()!=null){
            sql.append(" AND a.DEVICESOURCE ='").append(vo.getDeviceSource()).append("' ");
        }
        if (vo.getStartTime()!=null&&vo.getEndTime()!=null){
            sql.append(" AND a.ALARMTIME BETWEEN '").append(vo.getStartTime()).append(" 00:00:00").append("' \n").append("\tAND '").append(vo.getEndTime()).append(" 23:59:59").append("' ");
        }
        if (vo.getAlarmContentId()!=null){
            sql.append(" AND a.ALARMCONTENTID ='").append(vo.getAlarmContentId()).append("' ");
        }
        if (vo.getDeviceName()!=null){
            sql.append(" AND a.DEVICENAME like '%").append(vo.getDeviceName()).append("%' ");
        }
        if (vo.getHandleStatus()!=null){
            sql.append(" AND a.HANDLESTATUS ='").append(vo.getHandleStatus()).append("' ");
        }
        if (CollUtil.isNotEmpty(vo.getHandleStatusList())){
            sql.append(" AND a.handleStatus IN ('").append(StrUtil.join("','",vo.getHandleStatusList())).append("') ");
        }
        sql.append(" GROUP BY a.ALARMCATEGORY");
        return sql.toString();
    }


    public String groupAlarmLevel(AlarmRecordsVo vo){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) num,a.ALARMLEVEL FROM ALARM_RECORDS a  WHERE 1=1 AND a.ISDEL=0 " );
        if (vo.getAlarmType()!=null){
            sql.append(" AND a.ALARMTYPE ='").append(vo.getAlarmType()).append("' ");
        }
        if (vo.getAreaId()!=null){
            sql.append(" AND a.AREAID ='").append(vo.getAreaId()).append("' ");
        }
        if (vo.getFloorId()!=null){
            sql.append(" AND a.FLOORID ='").append(vo.getFloorId()).append("' ");
        }
        if (vo.getAlarmCategory()!=null){
            sql.append(" AND a.ALARMCATEGORY ='").append(vo.getAlarmCategory()).append("' ");
        }
        if (vo.getDeviceSource()!=null){
            sql.append(" AND a.DEVICESOURCE ='").append(vo.getDeviceSource()).append("' ");
        }
        if (vo.getStartTime()!=null&&vo.getEndTime()!=null){
            sql.append(" AND a.ALARMTIME BETWEEN '").append(vo.getStartTime()).append(" 00:00:00").append("' \n").append("\tAND '").append(vo.getEndTime()).append(" 23:59:59").append("' ");
        }
        if (vo.getAlarmContentId()!=null){
            sql.append(" AND a.ALARMCONTENTID ='").append(vo.getAlarmContentId()).append("' ");
        }
        if (vo.getDeviceName()!=null){
            sql.append(" AND a.DEVICENAME like '%").append(vo.getDeviceName()).append("%' ");
        }
        if (vo.getHandleStatus()!=null){
            sql.append(" AND a.HANDLESTATUS ='").append(vo.getHandleStatus()).append("' ");
        }
        if (CollUtil.isNotEmpty(vo.getHandleStatusList())){
            sql.append(" AND a.handleStatus IN ('").append(StrUtil.join("','",vo.getHandleStatusList())).append("') ");
        }
        sql.append(" GROUP BY a.ALARMLEVEL");
        return sql.toString();
    }

    public String line(AlarmRecordsVo vo){
        StringBuilder sql = new StringBuilder("SELECT TO_CHAR(TO_DATE(a.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD') time ,COUNT(1) num\n" +
                "FROM ALARM_RECORDS a\n" +
                "WHERE 1=1  AND a.ISDEL=0  AND TO_DATE(a.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') BETWEEN TRUNC(SYSDATE) - 6 AND TRUNC(SYSDATE) + 1");
        if (vo.getAlarmType()!=null){
            sql.append(" AND a.ALARMTYPE ='").append(vo.getAlarmType()).append("' ");
        }
        if (vo.getAreaId()!=null){
            sql.append(" AND a.AREAID ='").append(vo.getAreaId()).append("' ");
        }
        if (vo.getFloorId()!=null){
            sql.append(" AND a.FLOORID ='").append(vo.getFloorId()).append("' ");
        }
        if (vo.getAlarmCategory()!=null){
            sql.append(" AND a.ALARMCATEGORY ='").append(vo.getAlarmCategory()).append("' ");
        }
        if (vo.getDeviceSource()!=null){
            sql.append(" AND a.DEVICESOURCE ='").append(vo.getDeviceSource()).append("' ");
        }
        if (vo.getStartTime()!=null&&vo.getEndTime()!=null){
            sql.append(" AND a.ALARMTIME BETWEEN '").append(vo.getStartTime()).append(" 00:00:00").append("' \n").append("\tAND '").append(vo.getEndTime()).append(" 23:59:59").append("' ");
        }
        if (vo.getAlarmContentId()!=null){
            sql.append(" AND a.ALARMCONTENTID ='").append(vo.getAlarmContentId()).append("' ");
        }
        if (vo.getDeviceName()!=null){
            sql.append(" AND a.DEVICENAME like '%").append(vo.getDeviceName()).append("%' ");
        }
        if (vo.getHandleStatus()!=null){
            sql.append(" AND a.HANDLESTATUS ='").append(vo.getHandleStatus()).append("' ");
        }
        sql.append("GROUP BY   TO_CHAR(TO_DATE(a.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD') ");
        return sql.toString();
    }

    public String handleTime(String sql,AlarmStatisticsVo vo){
        if (Objects.equals(vo.getRadio(),"1")){
            sql+= "    AND ar.ALARMTIME >= '"+vo.getStartTime()+" 00:00:00' \n" +
                    "    AND ar.ALARMTIME <= '"+vo.getEndTime()+" 23:59:59'";
//            sql+= "    AND ar.ALARMTIME  LIKE ='"+ DateUtil.format(new Date(),"yyyy-MM-dd")+"%' ";
        }else if (Objects.equals(vo.getRadio(),"2")){
//            sql+= "    AND ar.ALARMTIME  LIKE ='"+ DateUtil.format(new Date(),"yyyy-MM")+"-%' ";
            sql+= "    AND ar.ALARMTIME  LIKE '"+ vo.getStartTime()+"-%' ";
        }else if (Objects.equals(vo.getRadio(),"3")){
//            sql+= "    AND ar.ALARMTIME  LIKE ='"+ DateUtil.format(new Date(),"yyyy")+"-%' ";
            sql+= "    AND ar.ALARMTIME  LIKE '"+ vo.getStartTime()+"-%' ";
        }else if (Objects.equals(vo.getRadio(),"4")){
            sql+= "    AND ar.ALARMTIME >= '"+vo.getStartTime()+" 00:00:00' \n" +
                    "    AND ar.ALARMTIME <= '"+vo.getEndTime()+" 23:59:59'";
        }else {
            sql+="";
        }
        return sql;
    }


    /**
     * 告警类型统计
     * 类型排名
     * @param vo
     * @return
     */
    public String pieChart(AlarmStatisticsVo vo){
        String sql = "SELECT \n" +
                "    ac.NAME,\n" +
                "    COUNT(ar.ID) AS VALUE ,ac.id\n" +
                "FROM \n" +
                "    ALARM_CATEGORY ac \n" +
                "LEFT JOIN ALARM_RECORDS ar \n" +
                "    ON ac.id  = ar.ALARMCATEGORY ";
        sql = handleTime(sql,vo);
        sql+= "    AND ar.ISDEL = 0 \n" +
                "WHERE 1=1 \n" +
                "   AND ac.ISDEL = 0 \n" ;
        if (StrUtil.isNotBlank(vo.getAlarmType())){
            sql+= " AND ar.ALARMTYPE = '"+vo.getAlarmType()+"' ";
        }
        if (null != vo && null != vo.getHandleStatus() && vo.getHandleStatus().size() > 0){
            sql+= " AND ar.handleStatus  in ("+StringUtils.list2str(vo.getHandleStatus())+") ";
        }

        sql+= " \nGROUP BY\n" +
                "    ac.NAME ,ac.id\n" +
                "ORDER BY VALUE DESC";
        return sql;
    }
    public String pieChartNoRank(AlarmStatisticsVo vo){
        String sql = "SELECT \n" +
                "    ac.NAME,\n" +
                "    COUNT(ar.ID) AS VALUE ,ac.id,ac.code as code\n" +
                "FROM \n" +
                "    ALARM_CATEGORY ac \n" +
                "LEFT JOIN ALARM_RECORDS ar \n" +
                "    ON ac.id  = ar.ALARMCATEGORY ";
        sql = handleTime(sql,vo);
        sql+= "    AND ar.ISDEL = 0 \n" +
                "WHERE 1=1 \n" +
                "  AND  ac.ISDEL = 0 \n" ;
        if (StrUtil.isNotBlank(vo.getAlarmType())){
            sql+= " AND ar.ALARMTYPE = '"+vo.getAlarmType()+"' ";
        }

        if (null != vo && null != vo.getHandleStatus() && vo.getHandleStatus().size() > 0){
            sql+= " AND ar.handleStatus  in ("+StringUtils.list2str(vo.getHandleStatus())+") ";
        }
        sql+=   " GROUP BY\n" +
                "    ac.NAME ,ac.id,ac.code\n";
        return sql;
    }


    /**
     * 区域排名
     * @param vo
     * @return
     */
    public String areaRank(AlarmStatisticsVo vo){
        String sql = "SELECT\n" +
                "\tsba.AREANAME as NAME,\n" +
                "\tCOUNT( ar.AREAID ) AS VALUE ,sba.ID as ID\n" +
                "FROM\n" +
                "\tSYS_BUILD_AREA sba\n" +
                "\tLEFT JOIN ALARM_RECORDS ar ON sba.id = ar.AREAID AND ar.ISDEL = 0 ";
        sql = handleTime(sql,vo);
        sql+= "   WHERE 1=1 \n" +
                "\t AND sba.ISDEL = 0 \n" ;
        if (StrUtil.isNotBlank(vo.getAlarmType())){
            sql+= " AND ar.ALARMTYPE = '"+vo.getAlarmType()+"' ";
        }
        sql+=    " GROUP BY\n" +
                "\tsba.AREANAME,sba.ID\n" +
                "\tORDER BY  VALUE desc";
        return sql;
    }

    /**
     * 等级排名
     * @param vo
     * @return
     */
    public String levelRank(AlarmStatisticsVo vo){
        String sql = "SELECT \n" +
                "  d.DICTLABEL  AS NAME,\n" +
                "  COUNT(ar.alarmLevel)  AS VALUE , d.DICTVALUE as id\n" +
                "FROM SYS_DICT d \n" +
                "LEFT JOIN ALARM_RECORDS ar \n" +
                "  ON d.DICTVALUE  = ar.alarmLevel  \n" +
                "  AND ar.ISDEL = 0 \n";
        sql = handleTime(sql,vo);
        sql+= "   WHERE 1=1 AND d.DICTTYPE  = 'alarmLevel'  \n" ;
        if (StrUtil.isNotBlank(vo.getAlarmType())){
            sql+= " AND ar.ALARMTYPE = '"+vo.getAlarmType()+"' ";
        }
        sql+=   "GROUP BY d.DICTLABEL , d.DICTVALUE \n" +
                "ORDER BY VALUE DESC";
        return sql;
    }

    /**
     * 告警总数查询
     * @param vo
     * @return
     */
    public String alarmNum(AlarmStatisticsVo vo){
        String sql = "SELECT\n" +
                "\tcount(ar.id) as VALUE\n" +
                "FROM\n" +
                "\tALARM_RECORDS ar\n" +
                "\tLEFT JOIN ALARM_CATEGORY ac ON ar.ALARMCATEGORY = ac.id  AND ac.ISDEL = 0 \n";
        sql+= "   WHERE 1=1 \n" +
                "\tAND ar.ISDEL = 0 \n" ;
        sql = handleTime(sql,vo);
        if (StrUtil.isNotBlank(vo.getAlarmCode())){
            sql+=  " AND ac.CODE = '"+vo.getAlarmCode()+"' ";
        }
        if (CollUtil.isNotEmpty(vo.getHandleStatus())){
            sql+= " AND ar.handleStatus in ('"+ StrUtil.join("','",vo.getHandleStatus())+"')";
        }
        return sql;
    }

    /**
     * 小趋势图 日
     * @param vo
     * @return
     */
    public String trendChartDay(AlarmStatisticsVo vo){
        String sql = "WITH date_range AS (\n" +
                "    SELECT TO_DATE('"+vo.getStartTime()+"', 'YYYY-MM-DD') + LEVEL - 1 AS full_date \n" +
                "    FROM dual \n" +
                "    CONNECT BY LEVEL <= TO_DATE('"+vo.getEndTime()+"', 'YYYY-MM-DD') - TO_DATE('"+vo.getStartTime()+"', " +
                "'YYYY-MM-DD') + 1 \n" +
                ")\n" +
                "SELECT \n" +
                "    TO_CHAR(dr.full_date,  'YYYY-MM-DD') AS NAME,\n" ;
        if (StrUtil.isNotBlank(vo.getAlarmCode())) {
            sql += "    COUNT(CASE WHEN ac.CODE = '" + vo.getAlarmCode() + "' THEN ar.ID ELSE NULL END) AS VALUE  \n";
        } else {
            sql += "    COUNT(ar.ID) AS VALUE  \n";
        }
        sql += "FROM \n" +
                "    date_range dr \n" +
                "LEFT JOIN ALARM_RECORDS ar ON \n" +
                "    TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') = TO_CHAR(dr.full_date,  'YYYY-MM-DD') \n" +
                "    AND ar.ISDEL = 0 \n" +
                "LEFT JOIN ALARM_CATEGORY ac ON \n" +
                "    ar.ALARMCATEGORY = ac.id  \n" +
                "    AND ac.ISDEL = 0 \n" +
                "GROUP BY \n" +
                "    dr.full_date  \n" +
                "ORDER BY \n" +
                "    dr.full_date";
        return sql;
    }

    /**
     * 小趋势图 月
     * @param vo
     * @return
     */
    public String trendChartMonth(AlarmStatisticsVo vo){
        String sql = "\t\t\n" +
                "WITH params AS (\n" +
                "    SELECT \n" +
                "        TO_DATE('"+vo.getStartTime()+"', 'YYYY-MM-DD') AS year_start,  \n" +
                "        TO_DATE('"+vo.getStartTime()+"', 'YYYY-MM-DD') AS year_end   \n" +
                "    FROM dual \n" +
                "),\n" +
                "month_range AS (\n" +
                "    SELECT \n" +
                "        ADD_MONTHS(params.year_start,  LEVEL - 1) AS month_date,\n" +
                "        TO_CHAR(ADD_MONTHS(params.year_start,  LEVEL - 1), 'YYYY-MM') AS month_name\n" +
                "    FROM \n" +
                "        params\n" +
                "    CONNECT BY LEVEL <= 12 \n" +
                ")\n" +
                "SELECT \n" +
                "    mr.month_name  AS NAME,\n" ;
        if (StrUtil.isNotBlank(vo.getAlarmCode())) {
            sql += "    COUNT(CASE WHEN ac.CODE = '" + vo.getAlarmCode() + "' THEN ar.ID ELSE NULL END) AS VALUE  \n";
        } else {
            sql += "    COUNT(ar.ID) AS VALUE  \n";
        }
        sql+=   "FROM \n" +
                "    month_range mr\n" +
                "LEFT JOIN ALARM_RECORDS ar ON \n" +
                "    TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') BETWEEN TRUNC(mr.month_date,  'MM') AND LAST_DAY(mr.month_date) \n" +
                "    AND ar.ISDEL = 0\n" +
                "LEFT JOIN ALARM_CATEGORY ac ON \n" +
                "    ar.ALARMCATEGORY = ac.id  \n" +
                "    AND ac.ISDEL = 0\n" +
                "GROUP BY \n" +
                "    mr.month_name,  mr.month_date \n" +
                "ORDER BY \n" +
                "    mr.month_date ";
        return sql;
    }


//    public String distribution(AlarmStatisticsVo vo){
//        String sql = "WITH \n" +
//                "date_range AS (\n" +
//                "    SELECT TRUNC(SYSDATE) - LEVEL + 1 AS day_date \n" +
//                "    FROM DUAL\n" +
//                "    CONNECT BY LEVEL <= 7 \n" +
//                "),\n" +
//                "hour_range AS (\n" +
//                "    SELECT LPAD(LEVEL-1, 2, '0') AS hour_num \n" +
//                "    FROM DUAL \n" +
//                "    CONNECT BY LEVEL <= 24\n" +
//                "),\n" +
//                "date_hour_matrix AS (\n" +
//                "    SELECT \n" +
//                "        TO_CHAR(d.day_date,  'YYYY-MM-DD') AS full_date,\n" +
//                "        h.hour_num  AS full_hour \n" +
//                "    FROM date_range d \n" +
//                "    CROSS JOIN hour_range h \n" +
//                "),\n" +
//                "alarm_stats AS (\n" +
//                "    SELECT \n" +
//                "        TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') AS alarm_date,\n" +
//                "        TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'HH24') AS alarm_hour,\n" +
//                "        COUNT(*) AS alarm_count \n" +
//                "    FROM ALARM_RECORDS ar \n" +
//                "    WHERE TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') >= TRUNC(SYSDATE) - 6 \n" +
//                "    AND TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') < TRUNC(SYSDATE) + 1 \n" +
//                "    GROUP BY \n" +
//                "        TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD'),\n" +
//                "        TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'HH24')\n" +
//                ")\n" +
//                "SELECT \n" +
//                "    m.full_date  AS alarmDate,\n" +
//                "    m.full_hour  AS alarmHour,\n" +
//                "    NVL(a.alarm_count,  0) AS value\n" +
//                "FROM date_hour_matrix m \n" +
//                "LEFT JOIN alarm_stats a ON m.full_date  = a.alarm_date  AND m.full_hour  = a.alarm_hour  \n" +
//                "ORDER BY m.full_date,  m.full_hour ";
//        return sql;
//    }

    /**
     * 告警总数
     * @param vo
     * @return
     */
    public String averageNumber(AlarmStatisticsVo vo){
        String sql = "SELECT COUNT(*) AS VALUE\n" +
                "FROM ALARM_RECORDS ar\n" +
                "WHERE 1=1";
        sql = handleTime(sql,vo);
        return sql;
    }

    public String securityAssessment(AlarmStatisticsVo vo){
        StringBuilder sb = new  StringBuilder();
        sb.append(" select ar.id,ar.handleStatus,ar.handleTime,ar.alarmTime  from ALARM_RECORDS ar ");
        sb.append(" LEFT JOIN ALARM_CATEGORY  ac on ar.ALARMCATEGORY = ac.id ");
        sb.append(" LEFT JOIN ALARM_GROUP ag on ac.alarmgroup = ag.id ");
        sb.append(" where 1=1 ");
        if(StrUtil.isNotBlank(vo.getGroupId())) {
            sb.append(" and ag.id = '").append(vo.getGroupId()).append("'");
        }
        if(StrUtil.isNotBlank(vo.getStartTime())) {
            sb.append(" and ar.ALARMTIME >= '").append(vo.getStartTime()).append("'");
        }
        if(StrUtil.isNotBlank(vo.getEndTime())) {
            sb.append("  and ar.ALARMTIME<= '").append(vo.getEndTime()).append("'");
        }
        return sb.toString();
    }

    /**
     * 告警分布图
     * @param startTime
     * @param endTime
     * @param radio
     * @return
     */
    public  String distribution(String startTime, String endTime, String radio) {
        StringBuilder sql = new StringBuilder();

        switch (radio) {
            case "1": // 小时颗粒度
                sql.append("WITH  date_range AS ( ")
                        .append("  SELECT TRUNC(TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS')) + LEVEL - 1 AS day_date ")
                        .append("  FROM DUAL ")
                        .append("  CONNECT BY LEVEL <= TRUNC(TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS')) - TRUNC(TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS')) + 1 ")
                        .append("), ")
                        .append("hour_range AS ( ")
                        .append("  SELECT LPAD(LEVEL - 1, 2, '0') AS hour_num ")
                        .append("  FROM DUAL ")
                        .append("  CONNECT BY LEVEL <= 24 ")
                        .append("), ")
                        .append("date_hour_matrix AS ( ")
                        .append("  SELECT TO_CHAR(d.day_date,  'YYYY-MM-DD') AS full_date, h.hour_num  AS full_hour ")
                        .append("  FROM date_range d ")
                        .append("  CROSS JOIN hour_range h ")
                        .append("), ")
                        .append("alarm_stats AS ( ")
                        .append("  SELECT TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') AS alarm_date, ")
                        .append("         TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'HH24') AS alarm_hour, ")
                        .append("         COUNT(*) AS alarm_count ")
                        .append("  FROM ALARM_RECORDS ar ")
                        .append("  WHERE TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') >= TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("    AND TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') <= TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("  GROUP BY TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD'), ")
                        .append("           TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'HH24') ")
                        .append(") ")
                        .append("SELECT m.full_date  AS alarmDate, m.full_hour  AS alarmHour, NVL(a.alarm_count,  0) AS value ")
                        .append("FROM date_hour_matrix m ")
                        .append("LEFT JOIN alarm_stats a ON m.full_date  = a.alarm_date  AND m.full_hour  = a.alarm_hour  ")
                        .append("WHERE TO_DATE(m.full_date  || ' ' || m.full_hour,  'YYYY-MM-DD HH24') >= TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("  AND TO_DATE(m.full_date  || ' ' || m.full_hour,  'YYYY-MM-DD HH24') <= TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("ORDER BY m.full_date,  m.full_hour");
                break;

            case "2": // 天颗粒度
                sql.append("WITH  date_range AS ( ")
                        .append("  SELECT TRUNC(TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS')) + LEVEL - 1 AS day_date ")
                        .append("  FROM DUAL ")
                        .append("  CONNECT BY LEVEL <= TRUNC(TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS')) - TRUNC(TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS')) + 1 ")
                        .append("), ")
                        .append("alarm_stats AS ( ")
                        .append("  SELECT TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') AS alarm_date, ")
                        .append("         COUNT(*) AS alarm_count ")
                        .append("  FROM ALARM_RECORDS ar ")
                        .append("  WHERE TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') >= TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("    AND TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') <= TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("  GROUP BY TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD') ")
                        .append(") ")
                        .append("SELECT TO_CHAR(d.day_date,  'YYYY-MM-DD') AS alarmDate, ")
                        .append("       '00' AS alarmHour, ")
                        .append("       NVL(a.alarm_count,  0) AS value ")
                        .append("FROM date_range d ")
                        .append("LEFT JOIN alarm_stats a ON TO_CHAR(d.day_date,  'YYYY-MM-DD') = a.alarm_date  ")
                        .append("ORDER BY d.day_date");
                break;

            case "3": // 月颗粒度
                sql.append("WITH  month_range AS ( ")
                        .append("  SELECT ADD_MONTHS(TRUNC(TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS'), 'MM'), LEVEL - 1) AS month_date ")
                        .append("  FROM DUAL ")
                        .append("  CONNECT BY LEVEL <= MONTHS_BETWEEN( ")
                        .append("    TRUNC(TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS'), 'MM'), ")
                        .append("    TRUNC(TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS'), 'MM') ")
                        .append("  ) + 1 ")
                        .append("), ")
                        .append("alarm_stats AS ( ")
                        .append("  SELECT TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM') AS alarm_month, ")
                        .append("         COUNT(*) AS alarm_count ")
                        .append("  FROM ALARM_RECORDS ar ")
                        .append("  WHERE TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') >= TO_DATE('").append(startTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("    AND TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS') <= TO_DATE('").append(endTime).append("', 'YYYY-MM-DD HH24:MI:SS') ")
                        .append("  GROUP BY TO_CHAR(TO_DATE(ar.ALARMTIME, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM') ")
                        .append(") ")
                        .append("SELECT TO_CHAR(m.month_date,  'YYYY-MM') AS alarmDate, ")
                        .append("       '00' AS alarmHour, ")
                        .append("       NVL(a.alarm_count,  0) AS value ")
                        .append("FROM month_range m ")
                        .append("LEFT JOIN alarm_stats a ON TO_CHAR(m.month_date,  'YYYY-MM') = a.alarm_month  ")
                        .append("ORDER BY m.month_date");
                break;

            default:
                throw new IllegalArgumentException("Invalid radio value: " + radio);
        }

        return sql.toString();
    }

//    public static void main(String[] args) {
//        String sql = buildAlarmStatsSql("2025-01-01 00:00:00", "2025-12-31 23:59:59", 3);
//        System.out.println(sql);
//// 这将生成按小时统计5月1日到5月24日报警数据的SQL
//    }
}
