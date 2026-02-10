package com.jsdc.iotpt.dao;

import org.springframework.stereotype.Repository;


@Repository
public class MissionCareDao {


    public String countCare(Integer userId) {
        String sql = "SELECT * FROM MISSION_CARE mc LEFT JOIN MISSION m ON m.ID = mc.MISSIONID WHERE mc.CARETYPE = 2 AND m.IS_DEL = 0 AND  mc.CREATEUSER = " + userId + "";
        return sql;
    }
}
