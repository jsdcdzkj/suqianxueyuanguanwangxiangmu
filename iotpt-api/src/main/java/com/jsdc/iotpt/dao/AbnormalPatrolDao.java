package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.ConfigDeviceSignalMap;
import com.jsdc.iotpt.model.operate.AbnormalPatrol;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class AbnormalPatrolDao {
    public String getPageList(Page page, AbnormalPatrol bean) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("ap.ID,\n");
        sql.append("ap.ABNORMALLEVEL,\n");
        sql.append("ap.ABNORMALDESCRIPTION,\n");
        sql.append("ap.DEVICENAME,\n");
        sql.append("ap.ABNORMALTIME,\n");
        sql.append("ap.HANDLERESULT,\n");
        sql.append("ap.COMMENTS,\n");
        sql.append("ma.JOBPLANNAME,\n");
        sql.append("ma.TASKTYPE,\n");
        sql.append("ma.TEAMGROUPSID,\n");
        sql.append("ma.MISSIONSTATES\n");
        sql.append("FROM\n" +
                "\tABNORMAL_PATROL ap\n");
        sql.append("LEFT JOIN MISSION_ASSIGN ma ON ap.missionId = ma.missionId\n");
        sql.append("WHERE ma.STATE=1\n");
        // 处理结果
        if (null !=bean.getHandleResult()) {
            sql.append(" AND ap.HANDLERESULT = ").append(bean.getHandleResult());
        }
        // 任务名称
        if (StringUtils.hasText(bean.getJobPlanName())) {
            sql.append(" AND ma.JOBPLANNAME LIKE '%").append(bean.getJobPlanName()).append("%'");
        }
        // 班组ID
        if (null != bean.getTeamGroupId()) {
            sql.append(" AND ma.TEAMGROUPSID = ").append(bean.getTeamGroupId());
        }
        // 任务类型值
        if (null != bean.getTaskType()) {
            sql.append(" AND ma.TASKTYPE = ").append(bean.getTaskType());
        }

        sql.append(" order by handleResult asc, ABNORMALLEVEL desc, abnormalTime desc");
        return sql.toString();
    }
}
