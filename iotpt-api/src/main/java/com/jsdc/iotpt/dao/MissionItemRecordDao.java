package com.jsdc.iotpt.dao;

import com.jsdc.iotpt.model.operate.Mission;
import org.springframework.stereotype.Repository;


@Repository
public class MissionItemRecordDao {


    public String selectByMissionIdGroup(Integer missionId) {
        StringBuilder builder = new StringBuilder();
        builder.append("select missionId,region,subContent from mission_item_record where 1=1  ");
        if (null != missionId) {
            builder.append(" and missionId = '" + missionId + "'");
        }
        builder.append(" group by missionId,subContent,region");
        return builder.toString();
    }


    public String getRecordByMissionId(Mission mission) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * from mission_item_record WHERE \"ID\" IN (SELECT \"MAX\"(id) from mission_item_record WHERE MISSIONID=" + mission.getId() + " GROUP BY REGION)");

        return sql.toString();
    }

    /**
     * 进度条统计
     *
     * @param missionId
     * @param isHandle
     * @return
     */
    public String selectMissionCount(Integer missionId, Integer isHandle) {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) from (  select region from mission_item_record where missionId='" + missionId + "' ");
        if (null != isHandle) {
            builder.append(" and isHandle = '" + isHandle + "'");
        }
        builder.append(" group by region  ) a");
        return builder.toString();
    }
}
