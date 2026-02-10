package com.jsdc.iotpt.dao;

import org.springframework.stereotype.Repository;

/**
 * @program: 园区物联网管控平台
 * @author: jxl
 * @create: 2024-11-26 08:53
 **/
@Repository
public class CurtainDao {

    public String selectByMeetingId(Integer meetingId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.* FROM CURTAIN c LEFT JOIN MEETING_SCENE_CONTROL msc ON c.ID = msc.DEVICEID WHERE msc.DEVICETYPE = 5 AND msc.ISDEL = 0 AND c.ISDEL = 0 AND msc.MEETINGID = ").append(meetingId);
        return sql.toString();
    }


}
