package com.jsdc.iotpt.dao;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.new_alarm.AlarmPlanConfig;
import com.jsdc.iotpt.vo.AlarmPlanConfigVO;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class AlarmPlanConfigDao {

    private static final String FIND_IN_SET = " (a.ALARMCONTENT LIKE '%,@@' OR a.ALARMCONTENT LIKE '@@,%' OR a.ALARMCONTENT like '%,@@,%' OR a.ALARMCONTENT = '@@') ";

    public String getPage(Page<AlarmPlanConfig> page, AlarmPlanConfigVO vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.ID, a.ALARMCONTENT, a.CREATETIME, a.CREATEUSER, a.ISDEL, a.LINKAGERULE, a.PLANTIMECONFIGID, a.UPDATETIME, a.UPDATEUSER, a.PUSHCONFIG, a.PUSHTYPE, a.PUSHUSER, a.CREATEUSERNAME, a.ENABLE, a.PLANNAME,");
        sql.append(" b.NAME AS PLANTIMECONFIGNAME");
        sql.append(" FROM ALARM_PLAN_CONFIG a");
        sql.append(" LEFT JOIN ALARM_PLAN_TIME_CONFIG b ON a.PLANTIMECONFIGID = b.ID");
        sql.append(" WHERE a.ISDEL = 0");
        if (CollUtil.isNotEmpty(vo.getContentIds())) {
            List<Integer> contentIds = vo.getContentIds();
            sql.append(" AND (");
            for (int i = 0; i < contentIds.size(); i++) {
                Integer id = contentIds.get(i);
                sql.append(FIND_IN_SET.replaceAll("@@", String.valueOf(id)));
                if (i < contentIds.size() - 1) {
                    sql.append(" OR ");
                }
            }
            sql.append(")");
        }
        return sql.toString();
    }

    public String selectByContentId(String contentId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.ID, a.ALARMCONTENT, a.CREATETIME, a.CREATEUSER, a.ISDEL, a.LINKAGERULE, a.PLANTIMECONFIGID, a.UPDATETIME, a.UPDATEUSER, a.PUSHCONFIG, a.PUSHTYPE, a.PUSHUSER, a.CREATEUSERNAME, a.ENABLE ");
        sql.append(" FROM ALARM_PLAN_CONFIG a");
        sql.append(" WHERE a.ISDEL = 0 AND ").append(FIND_IN_SET.replaceAll("@@", contentId));
        return sql.toString();
    }

}
