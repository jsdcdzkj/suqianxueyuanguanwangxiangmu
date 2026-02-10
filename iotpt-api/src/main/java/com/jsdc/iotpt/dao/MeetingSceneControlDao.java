package com.jsdc.iotpt.dao;

import org.springframework.stereotype.Repository;

/**
 * @Author：jxl
 * @Date：2024/6/13 13:43
 * @FileDesc：
 */
@Repository
public class MeetingSceneControlDao {

    public String getByDeviceTypeId(Integer deviceType, Integer deviceId){
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT SCR.* from MEETING_SCENE_CONTROL scr ");
        sql.append("LEFT JOIN MEETING_ROOM_CONFIG mrc ON SCR.MEETINGID=MRC.\"ID\" ");
        sql.append("WHERE SCR.ISDEL=0 AND MRC.ISDEL=0 ");
        sql.append("And DEVICETYPE='"+deviceType+"' ");
        sql.append("AND SCR.DEVICEID='"+deviceId+"' ");

        return sql.toString();
    }
}
