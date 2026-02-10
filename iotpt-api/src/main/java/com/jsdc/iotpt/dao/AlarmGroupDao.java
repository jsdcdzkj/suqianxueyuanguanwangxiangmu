package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.new_alarm.AlarmGroup;
import org.springframework.stereotype.Repository;

@Repository
public class AlarmGroupDao {
    public String selectGroupByFloorId(Integer id) {
        String sql = "SELECT\n" +
                "\tag.name , ag.code ,MAX(ar.ALARMLEVEL) as value \n" +
                "\tFROM\n" +
                "\tALARM_RECORDS ar\n" +
                "\tLEFT JOIN ALARM_CATEGORY ac ON ar.ALARMCATEGORY = ac.id \n" +
                "\tLEFT JOIN ALARM_GROUP ag  ON ac.ALARMGROUP = ag.id\n" +
                "\tWHERE\n" +
                "\t1 = 1 \n" +
                "\tAND ar.floorId = "+id+ "  \n" +
                "\tAND ar.HANDLESTATUS = 0 AND ar.ISDEL = 0 GROUP BY ag.name, ag.code";
        return sql;
    }
}
