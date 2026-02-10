package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.common.DateUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;


@Repository
public class MissionAssignDao {


    public String getGroupName(Integer missionId) {
        return "SELECT * FROM MISSION_ASSIGN ma LEFT JOIN TEAM_GROUPS tg ON ma.TEAMGROUPSID = tg.ID WHERE ma.MISSIONID = '" + missionId + "'";
    }

    public String selecMissiontList() {
        return "SELECT * FROM MISSION_ASSIGN ma LEFT JOIN MISSION m ON ma.MISSIONID = m.ID WHERE m.ISPENDING != 1 " +
                "AND m.is_remind = 0 AND m.STATES in (2,4) AND m.IS_DEL = 0 AND ma.DEADLINE <= TO_DATE('" + DateUtils.dateToStr(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ";
    }
}
