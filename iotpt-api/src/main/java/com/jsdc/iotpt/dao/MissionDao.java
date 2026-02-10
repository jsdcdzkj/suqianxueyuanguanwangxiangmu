package com.jsdc.iotpt.dao;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.operate.MissionTjVo;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.AlarmStatisticsVo;
import com.jsdc.iotpt.vo.ElectricalSafetyReportVo;
import com.jsdc.iotpt.vo.ElectricalSafetyReportVo2;
import com.jsdc.iotpt.vo.ReportManageVo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import com.jsdc.iotpt.vo.operate.WorkOrderVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;


@Repository
public class MissionDao {

    public String getErrorMissions(List<Integer> taskMissionIds) {

        String sql = "SELECT m.* FROM MISSION M " + "WHERE M.IS_DEL = 0 AND M .\"SOURCE\" = 3 " + "AND M .SOURCEID IN ( " + "SELECT MIR.\"ID\" FROM MISSION_ITEM_RECORD mir WHERE MIR.MISSIONID IN (" + StringUtils.join(taskMissionIds, ",") + ") AND MIR.EXECUTION = 2 " + ") ";

        return sql;
    }

    public String getMissionViews(ReportManageVo bean) {
        StringBuilder sql = new StringBuilder();

        sql.append("select m.*,MA.DEADLINE deadDate,ma.createTime aCreateTime,tg.name groupName,ma.taskType AS assignTaskType,ma.urgency AS urgency from MISSION m ");
        sql.append("LEFT JOIN MISSION_ASSIGN ma ON m.\"ID\"=MA.MISSIONID ");
        sql.append("LEFT JOIN TEAM_GROUPS tg ON ma.teamGroupsId=tg.id ");
        sql.append("WHERE m.IS_DEL='0' AND (m.STATES ='1' OR m.STATES ='2' OR m.STATES ='4' OR m.STATES ='3') ");
        if (StringUtils.isNotEmpty(bean.getStartTime())) {
            sql.append("and m.reportingTime >= TO_DATE('" + bean.getStartTime() + "', 'yyyy-MM-dd HH24:mi:ss') ");
        }

        if (StringUtils.isNotEmpty(bean.getEndTime())) {
            sql.append("and m.reportingTime <= TO_DATE('" + bean.getEndTime() + "', 'yyyy-MM-dd HH24:mi:ss') ");
        }

        sql.append("and (m.areaId in( " + StringUtils.join(bean.getAreaIds(), ",") + ") or (m.\"SOURCE\" =4 AND m.SOURCEID IN (SELECT DISTINCT PLANID from JOB_PLAN_AREA jpa WHERE JPA.\"TYPE\"='areaId' AND JPA.ISDEL='0' and jpa.areaId in(" + StringUtils.join(bean.getAreaIds(), ",") + "))))");


        return sql.toString();
    }

    /**
     * PC端任务库专用
     *
     * @param page
     * @param bean
     * @return
     */
    public static String pageListMission1(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT DISTINCT a.serviceType,a.id, a.openTime, a.assignedTime, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a" + ".states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity," + " a.deadline, a.teamGroupsId,a.createTime FROM( " + " " +
                "SELECT mis.serviceType,mis.ID, mis.openTime, mis.assignedTime, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis" + ".states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,mis.publicity from mission mis  " + " left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0 ");
        if (null != bean.getStates()) {
            if (0 != bean.getStates()) {
                builder.append(" AND mis.states ='" + bean.getStates() + "' ");
            }
            if (3 == bean.getStates()) {//已处理数据
                builder.append("  and mis.handleId is not null   ");
                if (null != bean.getTeamGroupsId()) {
                    builder.append("  and ass.teamGroupsId = '").append(bean.getTeamGroupsId()).append("' ");
                }
                if (null != bean.getIsAdmin() && bean.getIsAdmin() != 1) {
                    builder.append(" AND (mis.handleId ='" + bean.getUserId() + "' or ass.CREATEUSER='" + bean.getUserId() + "')");
                }

                if (null != bean.getHome_tab_type()) {
                    if (2 == bean.getHome_tab_type()) {//7天
                        builder.append(" and  mis.handleDate>=SYSDATE-7 AND mis.handleDate<sysdate ");
                    }
                    if (3 == bean.getHome_tab_type()) {//30天
                        builder.append(" and  mis.handleDate>=SYSDATE-30 AND mis.handleDate<sysdate ");
                    }
                }
            }
            if (0 == bean.getStates()) {//我的上报
                if (null != bean.getUserId()) {
                    builder.append(" AND mis.createUser ='").append(bean.getUserId()).append("' ");
                }
                builder.append(" AND mis.states ='").append(bean.getStates()).append("' ");
                builder.append(" and  mis.source !=4");
            }
        }
        builder.append("  ) a left join sys_user u on a.userId=u.id where 1=1 ");
        if (null != bean.getUrgency()) {//任务级别
            builder.append(" and a.urgency = '" + bean.getUrgency() + "'");
        }
        if (null != bean.getLevels()) {//任务级别
            builder.append(" and a.levels = '" + bean.getLevels() + "'");
        }

        if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
            builder.append(" and a.publicity =1 ");
        }
        if (null != bean.getAssignTaskType()) {
            if (null != bean.getAssignTask()) {
                if ("planType".equals(bean.getAssignTask())) {
                    builder.append(" and a.source =4 ");
                } else {
                    builder.append(" and a.source !=4 ");
                }
            }
            builder.append(" and a.assignTaskType = '" + bean.getAssignTaskType() + "'");
        }
        if (StringUtils.isNotEmpty(bean.getUserName())) {
            builder.append(" and u.realName like  '%" + bean.getUserName() + "%' ");
        }
        if (null != bean.getSource()) {
            builder.append(" and a.source = '" + bean.getSource() + "'");
        }
        if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
            builder.append(" and a.deadline >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
            builder.append(" and a.deadline <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
        }
        //6531 commit by wy
        if (StringUtils.isNotEmpty(bean.getStartTimeStatistics())) {//上报开始时间
            builder.append(" and a.reportingTime >= TO_DATE('" + bean.getStartTimeStatistics() + "','YYYY-MM-DD hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(bean.getEndTimeStatistics())) {//上报结束时间
            builder.append(" and a.reportingTime <= TO_DATE('" + bean.getEndTimeStatistics() + "','YYYY-MM-DD hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(bean.getStartTimeStatistics1())) {//上报开始时间
            builder.append(" and a.reportingTime >= TO_DATE('" + bean.getStartTimeStatistics1() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(bean.getEndTimeStatistics1())) {//上报结束时间
            builder.append(" and a.reportingTime <= TO_DATE('" + bean.getEndTimeStatistics1() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(bean.getDeviceCode())) {
            builder.append(" and a.deviceCode = '" + bean.getDeviceCode() + "'");
        }
        if (StringUtils.isNotEmpty(bean.getTitle())) {
            builder.append(" AND a.title like '%" + bean.getTitle() + "%' ");
        }
        if (StringUtils.isNotEmpty(bean.getNotes())) {
            builder.append(" AND a.notes like '%").append(bean.getNotes()).append("%' ");
        }
        builder.append(" order by a.reportingTime desc");
        return builder.toString();
    }

    /**
     * PC端专用SQL
     * 根据不同的状态切换页面数据
     * 1、待指派
     * 2、待处理
     * 3、已处理
     * 4、开启
     * 5、撤销
     *
     * @param page
     * @param bean
     * @return
     */
    public static String pageListMission(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT DISTINCT a.serviceType,a.id, a.openTime, a.assignedTime, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a" + ".states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity," + " a.deadline, a.teamGroupsId,a.createTime,a.isPending FROM( " + " " +
                "SELECT mis.serviceType,mis.ID, mis.openTime, mis.assignedTime, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis" + ".states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,mis.publicity,mis.isPending from mission mis  " + " left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0 ");
        if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
            if (null != bean.getHome_tab_type()) {
                if (1 == bean.getHome_tab_type()) {//逾期
                    builder.append(" and ass.DEADLINE<SYSDATE ");
                }
            }
            if (null != bean.getTeamGroupsId()) {
                builder.append("  and ass.teamGroupsId = '").append(bean.getTeamGroupsId()).append("' ");
            }
            if (null!= bean.getIsPending()){
                if (null!= bean.getIsPending()){
                    if (1 == bean.getIsPending()) {
                        builder.append("  and mis.isPending = 1");
                    }else {
                        builder.append("  and mis.isPending != 1");
                    }
                }
            }
            builder.append(" AND mis.states IN (2, 4) AND ass.teamGroupsId  IN ( SELECT WM_CONCAT(DISTINCT GROUPID) AS GROUPID from team_group_user where teamRole = '1'  ");
            if (null != bean.getUserId()) {
                builder.append("AND USERID = '" + bean.getUserId() + "' ");
            }
            builder.append(" GROUP BY GROUPID) UNION ALL  " + " SELECT mis.serviceType,mis.id, mis.openTime, mis.assignedTime, mis.notes, mis.levels, mis.source, mis.substance, mis.userId, mis.reportingTime, mis.states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency, ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate,mis.areaId,mis.createTime,mis.publicity,mis.isPending FROM mission mis" + " LEFT JOIN mission_assign ass ON mis.ID = ass.missionId " + " WHERE mis.is_del=0  AND mis.states = 2  ");
            if (null != bean.getHome_tab_type()) {
                if (1 == bean.getHome_tab_type()) {//逾期
                    builder.append(" and ass.DEADLINE<SYSDATE ");
                }
            }

            if (null!= bean.getIsPending()){
                if (1 == bean.getIsPending()) {
                    builder.append("  and mis.isPending = 1");
                }else {
                    builder.append("  and mis.isPending != 1");
                }
            }
            builder.append(" AND ass.teamGroupsId IN (select WM_CONCAT(DISTINCT GROUPID) from mission_assign_user " + " where 1=1 ");
            if (null != bean.getUserId()) {
                builder.append(" and USERID = '" + bean.getUserId() + "'");
            }
            builder.append(" GROUP BY GROUPID) ");
        } else {
            if (null != bean.getStates()) {
                if (0 != bean.getStates()) {
                    builder.append(" AND mis.states ='" + bean.getStates() + "' ");
                }
                if (3 == bean.getStates()) {//已处理数据
                    builder.append("  and mis.handleId is not null   ");
                    if (null != bean.getTeamGroupsId()) {
                        builder.append("  and ass.teamGroupsId = '").append(bean.getTeamGroupsId()).append("' ");
                    }
                    if (null != bean.getIsAdmin() && bean.getIsAdmin() != 1) {
                        builder.append(" AND (mis.handleId ='" + bean.getUserId() + "' or ass.CREATEUSER='" + bean.getUserId() + "')");
                    }

                    if (null != bean.getHome_tab_type()) {
                        if (2 == bean.getHome_tab_type()) {//7天
                            builder.append(" and  mis.handleDate>=SYSDATE-7 AND mis.handleDate<sysdate ");
                        }
                        if (3 == bean.getHome_tab_type()) {//30天
                            builder.append(" and  mis.handleDate>=SYSDATE-30 AND mis.handleDate<sysdate ");
                        }
                    }
                }
                if (0 == bean.getStates()) {//我的上报
                    if (null != bean.getUserId()) {
                        builder.append(" AND mis.createUser ='" + bean.getUserId() + "' ");
                    }
                    builder.append(" and  mis.source !=4");
                }
            }
        }
        builder.append("  ) a left join sys_user u on a.userId=u.id where 1=1 ");
        if (null != bean) {
            if (null != bean.getUrgency()) {//任务级别
                builder.append(" and a.urgency = '" + bean.getUrgency() + "'");
            }
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and a.levels = '" + bean.getLevels() + "'");
            }

            if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
                builder.append(" and a.publicity =1 ");
            }
            if (null != bean.getAssignTaskType()) {
                if (null != bean.getAssignTask()) {
                    if ("planType".equals(bean.getAssignTask())) {
                        builder.append(" and a.source =4 ");
                    } else {
                        builder.append(" and a.source !=4 ");
                    }
                }
                builder.append(" and a.assignTaskType = '" + bean.getAssignTaskType() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getUserName())) {
                builder.append(" and u.realName like  '%" + bean.getUserName() + "%' ");
            }
            if (null != bean.getSource()) {
                builder.append(" and a.source = '" + bean.getSource() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and a.deadline >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and a.deadline <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
            }
            //6531 commit by wy
            if (StringUtils.isNotEmpty(bean.getStartTimeStatistics())) {//上报开始时间
                builder.append(" and a.reportingTime >= TO_DATE('" + bean.getStartTimeStatistics() + "','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTimeStatistics())) {//上报结束时间
                builder.append(" and a.reportingTime <= TO_DATE('" + bean.getEndTimeStatistics() + "','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getStartTimeStatistics1())) {//上报开始时间
                builder.append(" and a.reportingTime >= TO_DATE('" + bean.getStartTimeStatistics1() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTimeStatistics1())) {//上报结束时间
                builder.append(" and a.reportingTime <= TO_DATE('" + bean.getEndTimeStatistics1() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getDeviceCode())) {
                builder.append(" and a.deviceCode = '" + bean.getDeviceCode() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTitle())) {
                builder.append(" AND a.title like '%" + bean.getTitle() + "%' ");
            }
            if (StringUtils.isNotEmpty(bean.getNotes())) {
                builder.append(" AND a.notes like '%" + bean.getNotes() + "%' ");
            }
        }
//        if (null != bean.getSelectSource()) {
//            builder.append(" order by a.reportingTime desc");
//        } else {
//            builder.append(" order by a.source,a.states");
//        }
        builder.append(" order by a.reportingTime desc");
        return builder.toString();
    }

    public String countHandle() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT DISTINCT a.id, a.openTime, a.assignedTime, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a.states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity, a.deadline, a.teamGroupsId,a.createTime FROM(  SELECT mis.ID, mis.openTime, ");
        builder.append("mis.assignedTime, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis.states,mis.title,mis.isReads, ass.taskType AS assignTaskType,  ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,");
        builder.append("mis.publicity from mission mis   left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0  AND mis.states ='3'   and mis.handleId is not null     ) a left join sys_user u on a.userId=u.id where 1=1  order by a.reportingTime desc");
        return builder.toString();
    }

    public String countAllTask() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT DISTINCT a.id, a.openTime, a.assignedTime, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a.states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity, a.deadline, a.teamGroupsId,a.createTime FROM(  SELECT mis.ID, mis.openTime,");
        builder.append("mis.assignedTime, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis.states,mis.title,mis.isReads, ass.taskType AS assignTaskType,  ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,");
        builder.append("mis.publicity from mission mis   left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0  and  mis.source !=4  ) a left join sys_user u on a.userId=u.id where 1=1  order by a.reportingTime desc");
        return builder.toString();
    }

    /**
     * PC端专用SQL
     * 根据不同的状态切换页面数据
     * 1、待指派
     * 2、待处理
     * 3、已处理
     * 4、开启
     * 5、撤销
     *
     * @param bean
     * @return
     */
    public static String listMission(MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT\n" +
                "CASE \n" +
                "    WHEN b.assignTaskType IN ( 3, 4 ) THEN\n" +
                "    'dangers' \n" +
                "    WHEN b.assignTaskType IN ( 1, 2 ) THEN\n" +
                "    'event' \n" +
                "  END AS name,\n" +
                "    COUNT( * ) AS count,\n" +
                getTimeGroupColumn(bean.getRadioVal()) + " AS timeGroup FROM(\n"
        );

        String sql = pageListMission(null, bean);
        builder.append(sql);

        builder.append("   ) b\n" +
                "GROUP BY\n" +
                "CASE\n" +
                "    \n" +
                "    WHEN b.assignTaskType IN ( 3, 4 ) THEN\n" +
                "    'dangers' \n" +
                "    WHEN b.assignTaskType IN ( 1, 2 ) THEN\n" +
                "    'event' \n" +
                "  END,\n" +
                getTimeGroupColumn(bean.getRadioVal()));
        return builder.toString();
    }

    private static String getTimeGroupColumn(String radioVal) {
        switch (radioVal) {
            case "1":
                return "TO_CHAR(b.deadline, 'HH24')";
            case "2":
                return "TO_CHAR(b.deadline, 'YYYY-MM-DD')";
            case "3":
                return "TO_CHAR(b.deadline, 'YYYY-MM-DD')";
            default:
                return "TO_CHAR(b.deadline, 'YYYY-MM')";
        }
    }


    public String pageCareMission(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" select a.ID, a.notes, a.LEVELS, a.SOURCE, a.substance, a.userId, a.reportingTime, a.states, " + "a.title, a.isReads, ass.taskType as assignTaskType, ass.urgency, a.handleDate, a.areaId, a.publicity, ass.deadline, ass.teamGroupsId, A.createTime " + " from MISSION_CARE mc LEFT JOIN MISSION a on mc.MISSIONID = a.ID LEFT JOIN MISSION_ASSIGN ass on ass.missionId =a.id " + "where a.is_del= 0 and mc.CREATEUSER='" + bean.getUserId() + "'");
        if (null != bean) {
            if (null != bean.getCareType()) {//任务级别
                builder.append(" and mc.careType = '" + bean.getCareType() + "'");
            }
            if (null != bean.getUrgency()) {//任务级别
                builder.append(" and a.urgency = '" + bean.getUrgency() + "'");
            }
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and a.levels = '" + bean.getLevels() + "'");
            }
//            if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
//                builder.append(" and a.publicity =1 ");
//            }
            if (null != bean.getAssignTaskType()) {//任务类型
                builder.append(" and ass.taskType = '" + bean.getAssignTaskType() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and a.source = '" + bean.getSource() + "'");
            }
            if (bean.getCareType() == 1) {
                if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                    builder.append(" and ass.deadline >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
                }
                if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                    builder.append(" and ass.deadline <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
                }
            }
            if (bean.getCareType() == 2) {
                if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                    builder.append(" and a.reportingTime >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
                }
                if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                    builder.append(" and a.reportingTime <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
                }
            }
            if (StringUtils.isNotEmpty(bean.getDeviceCode())) {
                builder.append(" and a.deviceCode = '" + bean.getDeviceCode() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTitle())) {
                builder.append(" AND a.title like '%" + bean.getTitle() + "%' ");
            }
        }
        builder.append(" order by a.createTime desc");
        return builder.toString();
    }


    //数量
    public String dclCount(MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT count(DISTINCT a.id) FROM( SELECT mis.ID,mis.publicity from mission mis left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0   ");
        if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
            builder.append(" AND mis.states IN (2, 4) AND ass.teamGroupsId  IN ( SELECT WM_CONCAT(DISTINCT GROUPID) AS GROUPID from team_group_user where teamRole = '1' " + " ");
            if (null != bean.getUserId()) {
                builder.append("AND USERID = '" + bean.getUserId() + "'");
            }
            if (null!=bean.getIsPending()){
                builder.append(" and isPending='"+bean.getIsPending()+"' ");
            }else {
                builder.append(" and (isPending='0' or isPending is null)  ");
            }
            builder.append(" GROUP BY GROUPID) " + " UNION ALL  " + " SELECT mis.id,mis.publicity FROM mission mis" + " " +
                    "LEFT JOIN mission_assign ass ON mis.ID = ass.missionId " + " " +
                    "WHERE mis.is_del=0  AND mis.states = 2 AND ass.teamGroupsId IN " +
                    "(select WM_CONCAT(DISTINCT GROUPID) from mission_assign_user " + " " +
                    " where 1=1 ");
            if (null != bean.getUserId()) {
                builder.append(" and  USERID = '" + bean.getUserId() + "'  ");
            }
            if (null!=bean.getIsPending()){
                builder.append(" and isPending='"+bean.getIsPending()+"' ");
            }else {
                builder.append(" and (isPending='0' or isPending is null)  ");
            }
            builder.append("GROUP BY GROUPID ) ");
        } else {
            if (null != bean.getStates()) {
                if (0 != bean.getStates()) {
                    builder.append(" AND mis.states ='" + bean.getStates() + "' ");
                }
                if (3 == bean.getStates()) {//已处理数据
                    builder.append("  and mis.handleId is not null   ");
                    if (null != bean.getIsAdmin() && bean.getIsAdmin() != 1) {
                        builder.append(" AND (mis.handleId ='" + bean.getUserId() + "' or ass.CREATEUSER='" + bean.getUserId() + "')");
                    }

                }
                if (0 == bean.getStates()) {//我的上报
                    if (null != bean.getUserId()) {
                        builder.append(" AND mis.createUser ='" + bean.getUserId() + "' ");
                    }
                    builder.append(" and  mis.source !=4");
                }
            }
        }
        builder.append("  ) a where 1=1 ");
        if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
            builder.append(" and a.publicity  =1  ");
        }
//        builder.append(" order by a.states,a.source");
        return builder.toString();
    }

    public String appRepairPageList(SysUser user, Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("select su.realName as userName,mis.id,mis.notes,mis.levels,mis.source,mis.substance,mis.userId,mis.reportingTime,mis.states,ass.taskType as assignTaskType, ");
        builder.append(" ass.urgency as urgency,ass.deadline as deadline,ass.teamGroupsId as teamGroupsId,mis.title,tg.name teamGroupsName ");
        builder.append(" from mission mis  left join mission_assign ass on mis.id = ass.missionId ");
        builder.append(" left join SYS_USER su on mis.userId=su.id ");
        builder.append(" LEFT JOIN TEAM_GROUPS tg ON ass.teamGroupsId=tg.id ");
        builder.append(" where mis.is_del = 0");
//        builder.append(" where 1 = 1");
        if (null != bean) {
            if (null != bean.getSource()) {
                builder.append(" and mis.source ='" + bean.getSource() + "'");
            }

            if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
                builder.append(" and mis.states in (1,2,4) ");
            } else {
                builder.append(" and mis.states in (3,5,6,7,8) ");
            }

            if (null != bean.getAssignTaskType()) {
                builder.append(" and ass.taskType='" + bean.getAssignTaskType() + "' ");
            }

            if (null != bean.getStates()) {
                builder.append(" and mis.states = '" + bean.getStates() + "'");
            }
            if ((!"admin".equals(user.getLoginName()))) {
                builder.append(" and mis.userId = '" + user.getId() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTeamGroupIds())) {
                builder.append(" and ass.teamGroupsId  in( " + StringUtils.join(bean.getTeamGroupIds(), ",") + ")");
            }
            if (StringUtils.isNotEmpty(bean.getUserName())) {
                builder.append(" and su.realName like '%" + bean.getUserName() + "%' ");
            }

            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and mis.reportingTime >= TO_DATE('" + bean.getStartTime() + " 00:00:00','yyyy-MM-dd HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and mis.reportingTime <= TO_DATE('" + bean.getEndTime() + " 23:59:59','yyyy-MM-dd HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getNotes())) {
                builder.append(" and (mis.title like '%" + bean.getNotes() + "%' or mis.notes like '%" + bean.getNotes() + "%' ) ");
            }
        }
        builder.append(" and mis.states in (1,2,3,4,5,6,7,8) ");

        if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
            builder.append(" order by states,reportingTime asc");
        } else {
            builder.append(" order by reportingTime desc");
        }

        return builder.toString();
    }


    public String appMissionPageList(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("select su.realName as userName,mis.id,mis.notes,mis.levels,mis.source,mis.substance,mis.userId,mis.reportingTime,mis.states,ass.taskType as assignTaskType, ");
        builder.append(" ass.urgency as urgency,ass.deadline as deadline,ass.teamGroupsId as teamGroupsId,mis.title ");
        builder.append(" from mission mis  left join mission_assign ass on mis.id = ass.missionId ");
        builder.append(" left join SYS_USER su on mis.userId=su.id ");
        builder.append(" where mis.is_del = 0");
//        builder.append(" where 1 = 1");
        if (null != bean) {
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and mis.levels = '" + bean.getLevels() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and mis.source ='" + bean.getSource() + "'");
            }

            if (null != bean.getStates()) {
                builder.append(" and mis.states = '" + bean.getStates() + "'");
            }
            if (null != bean.getUserId()) {
                builder.append(" and mis.userId = '" + bean.getUserId() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTeamGroupIds())) {
                builder.append(" and ass.teamGroupsId  in( " + StringUtils.join(bean.getTeamGroupIds(), ",") + ")");
            }
            if (StringUtils.isNotEmpty(bean.getUserName())) {
                builder.append(" and su.realName like '%" + bean.getUserName() + "%' ");
            }

            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and mis.reportingTime >= TO_DATE('" + bean.getStartTime() + "','YYYY-MM-DD HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and mis.reportingTime <= TO_DATE('" + bean.getEndTime() + "','YYYY-MM-DD HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getNotes())) {
                builder.append(" and (mis.title like '%" + bean.getNotes() + "%' or mis.notes like '%" + bean.getNotes() + "%' ) ");
            }
            if (StringUtils.isNotEmpty(bean.getKeyword())) {
                builder.append(" and (mis.notes like '%" + bean.getNotes() + "%' ) ");
            }
            if (bean.getSearchType() == 4) {
                builder.append(" and mis.states = 1 and mis.source!=4");
            } else if (bean.getSearchType() == 1 || bean.getSearchType() == 2 || bean.getSearchType() == 3) {
                builder.append(" and mis.states in (2,3,4) and mis.source!=4");
            } else {
                builder.append(" and mis.states in (1,2,3,4) and mis.source!=4");
            }
        } else {
            builder.append(" and mis.states in (1,2,3,4) and mis.source!=4");
        }
        builder.append(" order by reportingTime desc");
        return builder.toString();
    }


    public String pendingList(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("select su.realName as userName,mis.id,mis.notes,mis.levels,mis.source,mis.substance,mis.userId,mis.reportingTime,mis.states,ass.taskType as assignTaskType, ");
        builder.append(" ass.urgency as urgency,ass.deadline as deadline,ass.teamGroupsId as teamGroupsId,mis.title ");
        builder.append(" from mission mis  left join mission_assign ass on mis.id = ass.missionId ");
        builder.append(" left join SYS_USER su on mis.userId=su.id ");
        builder.append(" where mis.is_del = 0");
//        builder.append(" where 1 = 1");
        if (null != bean) {
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and mis.levels = '" + bean.getLevels() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and mis.source ='" + bean.getSource() + "'");
            }

            if (null != bean.getStates()) {
                builder.append(" and mis.states = '" + bean.getStates() + "'");
            }
            if (null != bean.getUserId()) {
                builder.append(" and mis.userId = '" + bean.getUserId() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTeamGroupIds())) {
                builder.append(" and ass.teamGroupsId  in( " + StringUtils.join(bean.getTeamGroupIds(), ",") + ")");
            }
            if (StringUtils.isNotEmpty(bean.getUserName())) {
                builder.append(" and su.realName like '%" + bean.getUserName() + "%' ");
            }

            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and mis.pendingTime >= TO_DATE('" + bean.getStartTime() + "','YYYY-MM-DD HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and mis.pendingTime <= TO_DATE('" + bean.getEndTime() + "','YYYY-MM-DD HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getNotes())) {
                builder.append(" and (mis.title like '%" + bean.getNotes() + "%' or mis.notes like '%" + bean.getNotes() + "%' ) ");
            }
            if (StringUtils.isNotEmpty(bean.getKeyword())) {
                builder.append(" and (mis.notes like '%" + bean.getNotes() + "%' ) ");
            }

        }
        builder.append(" and mis.states ='2' and mis.isPending='1' ");
        builder.append(" order by reportingTime desc");
        return builder.toString();
    }

    public String pendingCount( MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(*) ");
        builder.append(" from mission mis ");
        builder.append(" where mis.is_del = 0");
//        builder.append(" where 1 = 1");
        if (null != bean) {
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and mis.levels = '" + bean.getLevels() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and mis.source ='" + bean.getSource() + "'");
            }

            if (null != bean.getStates()) {
                builder.append(" and mis.states = '" + bean.getStates() + "'");
            }
            if (null != bean.getUserId()) {
                builder.append(" and mis.userId = '" + bean.getUserId() + "'");
            }


            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and mis.assignedTime >= TO_DATE('" + bean.getStartTime() + "','YYYY-MM-DD HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and mis.assignedTime <= TO_DATE('" + bean.getEndTime() + "','YYYY-MM-DD HH24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getNotes())) {
                builder.append(" and (mis.title like '%" + bean.getNotes() + "%' or mis.notes like '%" + bean.getNotes() + "%' ) ");
            }
            if (StringUtils.isNotEmpty(bean.getKeyword())) {
                builder.append(" and (mis.notes like '%" + bean.getNotes() + "%' ) ");
            }

        }
        builder.append(" and mis.states ='2' and mis.isPending='1' ");
        builder.append(" order by reportingTime desc");
        return builder.toString();
    }


    public String missionPageList(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("select mis.id,mis.notes,mis.levels,mis.source,mis.substance,mis.userId,mis.reportingTime,mis.states,ass.taskType as assignTaskType, ");
        builder.append(" ass.urgency as urgency,ass.deadline as deadline,ass.teamGroupsId as teamGroupsId ");
        builder.append(" from mission mis  left join mission_assign ass on mis.id = ass.missionId where mis.is_del=0  ");
        if (null != bean) {
            if (null != bean.getStates()) {
                builder.append(" and mis.states = '" + bean.getStates() + "'");
            }
            if (null != bean.getUserId()) {
                builder.append(" and mis.userId = '" + bean.getUserId() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTeamGroupIds())) {
                builder.append(" and ass.teamGroupsId  in( " + StringUtils.join(bean.getTeamGroupIds(), ",") + ")");
            }
        }


        return builder.toString();
    }

    //根据分组统计数据
    public String countGroupsId(List<Integer> teamgroupIds, List<Integer> states) {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) ");
        builder.append("  from mission mis  left join mission_assign ass on mis.id=ass.missionId where mis.is_del=0 ");
        if (!teamgroupIds.isEmpty() && teamgroupIds.size() > 0) {
            builder.append(" and ass.teamGroupsId  in( " + StringUtils.join(teamgroupIds, ",") + ")");
        }
        if (!states.isEmpty() && states.size() > 0) {
            builder.append(" and mis.states  in( " + StringUtils.join(states, ",") + ")");
        }
        return builder.toString();
    }

    public String getListByGroup(MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\tteamGroupsId ,\n" + "\ttaskType ,\n" + "\tcount(1) count\n" + "FROM\n" + "\tmission ms\n" + "LEFT JOIN mission_assign ma ON MS. ID = MA.missionId\n" + "where MA.state = '1' and ms.is_del = 0 \n" + "\t and to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') >= '" + missionVo.getTimeStart() + "'" + "AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "')<= '" + missionVo.getTimeEnd() + "'" + "group by teamGroupsId,taskType order by teamGroupsId";
        return sql;
    }

    public String stateCount(MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\t\n" + "\tcount(1) count\n" + "FROM\n" + "\tmission ms\n" + "LEFT JOIN mission_assign ma ON MS. ID = MA.missionId\n" + "where MA.state = '1' and ms.is_del = 0 and ma.teamGroupsId = '" + missionVo.getTeamGroupsId() + "' and ma.taskType = '" + missionVo.getTaskType() + "'" + "\t and to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') >= '" + missionVo.getTimeStart() + "'" + " AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "')<= '" + missionVo.getTimeEnd();
        if (!"2".equals(missionVo.getState())) {
            sql += "' and MS.states = " + missionVo.getState();
        } else {
            sql += "' and (MS.states = 2 or MS.states =4)";
        }


        return sql;
    }


    public String getList(Page page, MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\t\n" + "\tjp.planName,\n" + "ma.taskType,\n" + "MA.teamGroupsId,\n" + "MA.missionStates state,\n" + "jp.planStartTime,\n" + "MS.handleDate\n" + "FROM\n" + "\tmission ms\n" + "LEFT JOIN mission_assign ma ON MS. ID = MA.missionId\n" + "left join JOB_PLAN jp on MA.teamGroupsId = jp.groupId\n" + "where MA.state = '1' and ms.is_del = 0 \n" + "\t and to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') >= '" + missionVo.getTimeStart() + "'" + "AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "')<= '" + missionVo.getTimeEnd() + "'";
        return sql;
    }

    public String getList2(MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\t\n" + "\tjp.planName,\n" + "ma.taskType,\n" + "MA.teamGroupsId,\n" + "MA.missionStates state,\n" + "jp.planStartTime,\n" + "MS.handleDate\n" + "FROM\n" + "\tmission ms\n" + "LEFT JOIN mission_assign ma ON MS. ID = MA.missionId\n" + "left join JOB_PLAN jp on MA.teamGroupsId = jp.groupId\n" + "where MA.state = '1' and ms.is_del = 0\n" + "\t and to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') >= '" + missionVo.getTimeStart() + "'" + "AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "')<= '" + missionVo.getTimeEnd() + "'";
        return sql;
    }

    public String getCount(MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\tTO_CHAR (" + missionVo.getQuerryTimeString() + ", '" + missionVo.getFormate() + "') AS day,\n" + "\tCOUNT (*) AS COUNT\n" + "FROM\n" + "\tMISSION ms left join mission_assign ma on MS.id= ma.missionId \n" + "WHERE ms.is_del = 0\n" + "\t AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') >= '" + missionVo.getTimeStart() + "'" + "AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "')<= '" + missionVo.getTimeEnd() + "'";
        if (!"2".equals(missionVo.getState())) {
            sql += "and MS.states = " + missionVo.getState() + "\n";
        } else {
            sql += "and (MS.states = 2 or MS.states =4) ";
        }

        sql += "GROUP BY\n" + "\tTO_CHAR (" + missionVo.getQuerryTimeString() + ", '" + missionVo.getFormate() + "')";
        return sql;
    }

    public String finishingRate(MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\tTO_CHAR (" + missionVo.getQuerryTimeString() + ", '" + missionVo.getFormate() + "') AS day,\n" + "\tround(SUM( CASE ms.states WHEN 3 THEN 1 ELSE 0 END )/count(1),2)*100 finishingRate\n\n" + "FROM\n" + "\tMISSION ms left join mission_assign ma on MS.id= ma.missionId \n" + "WHERE  ms.is_del = 0\n" + "\tAND  to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') >= '" + missionVo.getTimeStart() + "'" + "AND to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "')<= '" + missionVo.getTimeEnd() + "'" + "GROUP BY\n" + "\tTO_CHAR (" + missionVo.getQuerryTimeString() + ", '" + missionVo.getFormate() + "')";
        return sql;
    }


    /**
     * 电气安全分析 派单各个状态数量统计
     *
     * @return
     */
    public String stateCountReport(ElectricalSafetyReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(*) as count ");
        sql.append(" from MISSION m ");
        sql.append(" left join WARNING_INFO ws on m.sourceId = ws.id ");
        sql.append(" where m.is_del=0 and m.source='1' ");
        if (StringUtils.isNotNull(vo)) {
//            if (StringUtils.isNotEmpty(vo.getStartTime())) {
//                sql.append(" and to_timestamp(ws.alertTime , 'yyyy-MM-dd HH24:MI:ss.ff' ) >=  to_date('").append(vo.getStartTime()).append("', 'yyyy-MM-dd HH24:MI:ss' )");
//            }
//            if (StringUtils.isNotEmpty(vo.getEndTime())) {
//                sql.append(" and to_timestamp(ws.alertTime , 'yyyy-MM-dd HH24:MI:ss.ff' ) <  to_date('").append(vo.getEndTime()).append("', 'yyyy-MM-dd HH24:MI:ss' )");
//            }
            if (StringUtils.isNotNull(vo.getBuildId())) {
                sql.append(" and ws.buildId = ").append(vo.getBuildId());
            }
            if (StringUtils.isNotNull(vo.getFloorId())) {
                sql.append(" and ws.floorId = ").append(vo.getFloorId());
            }
            if (StringUtils.isNotNull(vo.getAreaId())) {
                sql.append(" and ws.areaId = ").append(vo.getAreaId());
            }
            if (StringUtils.isNotNull(vo.getStates())) {
                if (vo.getStates() == 2) {
                    sql.append(" and (m.states = 2 or m.states = 4)");
                } else {
                    sql.append(" and m.states = ").append(vo.getStates());
                }
            }
        }
        return sql.toString();
    }

    /**
     * 电气安全分析 工单处理情况排名
     *
     * @return
     */
    public String getListTop(ElectricalSafetyReportVo vo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT g.id,\n" + "\tmax( g.name ) realName,\n" + "\tSUM( CASE m.states WHEN 2 THEN 1 WHEN 4 THEN 1 ELSE 0 END ) dclCount,\n" + "\tSUM( CASE m.states WHEN 3 THEN 1 ELSE 0 END ) yclCount ");
        sql.append(" from TEAM_GROUPS g");
        sql.append(" LEFT JOIN MISSION_ASSIGN a ON g.id = a.TEAMGROUPSID AND a.state = 1");
        sql.append(" LEFT JOIN MISSION m ON a.missionId = m.id AND m.is_del=0 and m.source = '1'");
        sql.append(" LEFT JOIN WARNING_INFO ws ON m.sourceId = ws.id");
        sql.append(" where g.isDel=0");
        if (StringUtils.isNotNull(vo)) {
//            if (StringUtils.isNotEmpty(vo.getStartTime())) {
//                sql.append(" and to_timestamp(ws.alertTime , 'yyyy-MM-dd HH24:MI:ss.ff' ) >=  to_date('").append(vo.getStartTime()).append("', 'yyyy-MM-dd HH24:MI:ss' )");
//            }
//            if (StringUtils.isNotEmpty(vo.getEndTime())) {
//                sql.append(" and to_timestamp(ws.alertTime , 'yyyy-MM-dd HH24:MI:ss.ff' ) <  to_date('").append(vo.getEndTime()).append("', 'yyyy-MM-dd HH24:MI:ss' )");
//            }
            if (StringUtils.isNotNull(vo.getBuildId())) {
                sql.append(" and ws.buildId = ").append(vo.getBuildId());
            }
            if (StringUtils.isNotNull(vo.getFloorId())) {
                sql.append(" and ws.floorId = ").append(vo.getFloorId());
            }
            if (StringUtils.isNotNull(vo.getAreaId())) {
                sql.append(" and ws.areaId = ").append(vo.getAreaId());
            }
        }
        sql.append(" GROUP BY g.id ");
        sql.append(" ORDER BY yclCount desc");
        return sql.toString();
    }


    public String getMission_type(String timeType, String timeStart, String timeEnd) {
        String sql = "SELECT\n" + "\tcount( * ) AS \"value\",\n" + "\tb.taskType \"name\"\n" + "FROM\n" + "\tMISSION a\n" + "\tLEFT JOIN MISSION_ASSIGN b ON a.id = b.missionId \n" + "\tAND b.state = '1' \n" + "WHERE a.is_del=0 and b.taskType is not null and a.states in (1,2,3,4)\n";
        if ("0".equals(timeType)) {
            if (StrUtil.isNotEmpty(timeStart)) {
                sql += "\tAND to_char(a.handleDate,'yyyy-MM-dd') >= '" + timeStart + "' \n";
            }
            if (StrUtil.isNotEmpty(timeEnd)) {
                sql += "\tAND to_char(a.handleDate,'yyyy-MM-dd') <= '" + timeEnd + "' \n";
            }
        } else {
            if (StrUtil.isNotEmpty(timeStart)) {
                sql += "\tAND to_char(a.reportingTime,'yyyy-MM-dd') >= '" + timeStart + "' \n";
            }
            if (StrUtil.isNotEmpty(timeEnd)) {
                sql += "\tAND to_char(a.reportingTime,'yyyy-MM-dd') <= '" + timeEnd + "' \n";
            }
        }
        sql += "GROUP BY b.TASKTYPE";

        return sql;
    }

    public String getMission_state(String timeType, String timeStart, String timeEnd) {
        String sql = "SELECT\n" + "\tsum(\"value\") as \"value\",\n" + "\t\"status\"\n" + "FROM\n" + "\t(SELECT\n" + "\tcount( * ) AS \"value\",\n" + "CASE WHEN a.states = 2 \n" + "\tOR a.states = 4 THEN\n" + "\t2 ELSE a.states \n" + "\tEND AS \"status\"" + "FROM\n" + "\tMISSION a\n" + "WHERE a.is_del=0  and a.states in (1,2,3,4)\n";
        if ("0".equals(timeType)) {
            if (StrUtil.isNotEmpty(timeStart)) {
                sql += "\tAND to_char(a.handleDate,'yyyy-MM-dd') >= '" + timeStart + "' \n";
            }
            if (StrUtil.isNotEmpty(timeEnd)) {
                sql += "\tAND to_char(a.handleDate,'yyyy-MM-dd') <= '" + timeEnd + "' \n";
            }
        } else {
            if (StrUtil.isNotEmpty(timeStart)) {
                sql += "\tAND to_char(a.reportingTime,'yyyy-MM-dd') >= '" + timeStart + "' \n";
            }
            if (StrUtil.isNotEmpty(timeEnd)) {
                sql += "\tAND to_char(a.reportingTime,'yyyy-MM-dd') <= '" + timeEnd + "' \n";
            }
        }
        sql += "GROUP BY a.states) \n" + "GROUP BY\n" + "\t\"status\"";

        return sql;
    }

    public String getMission_type_team(String timeType, String timeStart, String timeEnd) {
        String sql = "SELECT\n" + "\tcount( * ) AS value,\n" + "\tb.taskType, \n" + "\tb.teamGroupsId \n" + "FROM\n" + "\tMISSION a\n" + "\tLEFT JOIN MISSION_ASSIGN b ON a.id = b.missionId \n" + "\tAND b.state = '1' \n" + "WHERE a.is_del=0 and b.taskType is not null and b.teamGroupsId is not null\n";
        if ("0".equals(timeType)) {
            if (StrUtil.isNotEmpty(timeStart)) {
                sql += "\tAND to_char(a.handleDate,'yyyy-MM-dd') >= '" + timeStart + "' \n";
            }
            if (StrUtil.isNotEmpty(timeEnd)) {
                sql += "\tAND to_char(a.handleDate,'yyyy-MM-dd') <= '" + timeEnd + "' \n";
            }
        } else {
            if (StrUtil.isNotEmpty(timeStart)) {
                sql += "\tAND to_char(a.reportingTime,'yyyy-MM-dd') >= '" + timeStart + "' \n";
            }
            if (StrUtil.isNotEmpty(timeEnd)) {
                sql += "\tAND to_char(a.reportingTime,'yyyy-MM-dd') <= '" + timeEnd + "' \n";
            }
        }
        sql += "GROUP BY b.taskType,b.teamGroupsId";

        return sql;
    }


    public String getSendOrdersCount(String date) {
        String sql = "SELECT\n" + "\tcount(1) count\n" + "FROM\n" + "\tMISSION \n" + "WHERE is_del = 0\n" + "\t and states IN (1, 2, 3)\n" + "and \n" + "\tTO_CHAR (createTime, 'yyyy-MM-dd') = '" + date + "'";
        return sql;
    }

    public String getCompleteCount(String date) {
        String sql = "SELECT\n" + "\tcount(1) count\n" + "FROM\n" + "\tMISSION\n" + "WHERE is_del = 0\n" + "\t and states IN (3)\n" + "and \n" + "\tTO_CHAR (createTime, 'yyyy-MM-dd') = '" + date + "'";
        return sql;
    }

    public String numberGjOfDays() {
        String sql = " SELECT\n" + "\tdays.createTime createTime,\n" + "\tnvl( m.COUNT, 0 ) count \n" + "FROM\n" + "\t( SELECT to_char( SYSDATE - LEVEL + 1, 'yyyy-mm-dd' ) createTime FROM DUAL CONNECT BY LEVEL <= 7 ) days\n" + "\tLEFT JOIN (\n" + "\tSELECT\n" + "\t SUBSTR( o.ALERTTIME, 0, 10) createTime,\n" + "\t\tnvl( COUNT( 1 ), 0 ) count \n" + "\tFROM\n" + "\t\tWARNING_INFO o \n" + "\tGROUP BY\n" + "\t\t SUBSTR( o.ALERTTIME, 0, 10) \n" + "\t) m ON days.createTime = m.createTime \n" + "GROUP BY\n" + "\tdays.createTime,\n" + "\tm.count \n" + "ORDER BY\n" + "\tdays.createTime";
        return sql;
    }

    public String monthAlarm() {
        String sql = "select sd.DICTLABEL name, nvl(t.DEVNUM, 0) count from SYS_DICT sd LEFT JOIN (\n" + "SELECT\n" + "\tCOUNT( t1.WARNTYPE ) as devNum,\n" + "\tt1.WARNTYPE \n" + "FROM\n" + "\tWARNING_INFO t1 \n" + "WHERE\n" + "\tTRUNC( TO_DATE( SUBSTR( t1.ALERTTIME, 1, 10 ), 'yyyy-mm-dd' ) ) >= TRUNC( SYSDATE - 30 ) and t1.DEVICECOLLECTID is not null\n" + "GROUP BY t1.WARNTYPE\n" + ") t on t.WARNTYPE = sd.DICTVALUE\n" + " where sd.DICTTYPE = 'warnType' and sd.ISDEL = '0' ";
        return sql;
    }

    public String alarmStatisticsNum() {
        String sql = "select \n" + "\tsum(case when TRUNC( TO_DATE( SUBSTR( t1.ALERTTIME, 1, 10 ), 'yyyy-mm-dd' ) ) >= TRUNC( SYSDATE ) then 1 else 0 end) jrgj,\n" + "\tsum(case when TRUNC( TO_DATE( SUBSTR( t1.ALERTTIME, 1, 10 ), 'yyyy-mm-dd' ) ) >= TRUNC( SYSDATE -7 ) then 1 else 0 end) bzgj,\n" + "\tsum(case when TRUNC( TO_DATE( SUBSTR( t1.ALERTTIME, 1, 10 ), 'yyyy-mm-dd' ) ) >= TRUNC( SYSDATE -30 ) then 1 else 0 end) bygj\n" + "from WARNING_INFO t1";
        return sql;
    }

    public String equipmentStatisticsNum() {
        String sql = "select \n" + " count(*) znum, \n" + " sum(case when ONLINESTATUS = '1' then 1 ELSE 0  end) zcnum,\n" + " sum(case when ONLINESTATUS = '2' then 1 else 0 end) lxnum,\n" + " sum(case when ONLINESTATUS = '3' then 1 else 0 end) ycnum,\n" + "\tsum(case when status = '2' then 1 else 0 end) sbnum\n" + "from DEVICE_COLLECT where ISDEL = '0' ";
        return sql;
    }

    public String merAlarmNum(int num) {
        String sql = "select \n" + "\tcount( DISTINCT t1.AREAID )\n" + "from WARNING_INFO t1 where t1.AREAID is not null and TRUNC( TO_DATE( SUBSTR( t1.ALERTTIME, 1, 10 ), 'yyyy-mm-dd' ) ) >= TRUNC( SYSDATE -" + num + "  ) ";
        return sql;
    }


    public String rwqs(MissionTjVo missionVo) {
        String sql = "SELECT\n" + "\tTO_CHAR (" + missionVo.getQuerryTimeString() + ", '" + missionVo.getFormate() + "') AS day,\n" + "\n" + "SUM(CASE MS.states WHEN 1 THEN 1 ELSE 0 END) infoC0,\n" + "SUM(CASE MS.states WHEN 2 THEN 1 ELSE 0 END) infoC1,\n" + "SUM(CASE MS.states WHEN 3 THEN 1 ELSE 0 END) infoC2,\n" + "round(SUM( CASE ms.states WHEN 3 THEN 1 ELSE 0 END )/count(1),2)*100 finishingRate ,\n" + "\tCOUNT (*) AS COUNT\n" + "\n" + "FROM\n" + "\tMISSION ms left join mission_assign ma on MS.id= ma.missionId \n" + "WHERE ms.is_del = 0\n" + "\t and to_char(" + missionVo.getQuerryTimeString() + ",'" + missionVo.getFormate() + "') = '" + missionVo.getTimeStart() + "'\n" + "\n" + "GROUP BY\n" + "\tTO_CHAR (" + missionVo.getQuerryTimeString() + ", '" + missionVo.getFormate() + "')";
        return sql;
    }


    /**
     * 驾驶舱智慧大屏-工单统计
     * Author wzn
     * Date 2023/11/2 13:49
     */
    public String gdtjs() {
//        String sql = "\n" +
//                "                 SELECT \n" +
//                "                 COUNT (1) \n" +
//                "                 FROM \n" +
//                "                 MISSION M \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t M.IS_DEL='0' and\n" +
//                "                 m.states in(1,2,3,4)\n" +
//                "                 AND TO_CHAR (M .CREATETIME, 'yyyy') = ( \n" +
//                "                 SELECT \n" +
//                "                  EXTRACT (YEAR FROM SYSDATE) \n" +
//                "                 FROM \n" +
//                "                  dual \n" +
//                "                 ) \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 COUNT (1) \n" +
//                "                 FROM \n" +
//                "                 MISSION M \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t m.IS_DEL='0' and\n" +
//                "                m.states in(1,2,3,4)\n" +
//                "                 AND TO_CHAR (M .CREATETIME, 'mm') = ( \n" +
//                "                 SELECT \n" +
//                "                  EXTRACT (month FROM SYSDATE) \n" +
//                "                 FROM \n" +
//                "                  dual \n" +
//                "                 ) \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" +
//                "                 s.states in(1,2,4) and taskType = 3 and  s.source ='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE  s.IS_DEL='0' and\n" +
//                "                 s.states in(1,2,3,4) and taskType = 3 and  s.source ='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" +
//                "                 s.states in(1,2,4) and taskType = 2 and  s.source ='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" +
//                "                 s.states in(1,2,3,4) and taskType = 2 and  s.source ='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" +
//                "                 s.states in(1,2,4) and taskType = 1 and  s.source !='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE s.IS_DEL='0' and s.states in(1,2,3,4) and taskType = 1 and  s.source !='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE \n" +
//                "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" +
//                "                 s.states in(1,2,4) and taskType = 4 and  s.source !='4' \n" +
//                "                 UNION all \n" +
//                "                 SELECT \n" +
//                "                 count(1) \n" +
//                "                 FROM \n" +
//                "                 MISSION s \n" +
//                "                 left join MISSION_ASSIGN ma \n" +
//                "                 on MA.missionId = s.id \n" +
//                "                 WHERE s.IS_DEL='0' and s.states in(1,2,3,4) and taskType = 4and  s.source !='4' ";
//        return sql;
        String sql = "\n" + "                 SELECT \n" + "                COUNT (1) \n" + "                 FROM \n" + "                 MISSION M \n" + "\t\t\t\t\t\t\t\t LEFT JOIN MISSION_ASSIGN ma ON MA.missionId = M.id \n" + "                 WHERE \n" + "\t\t\t\t\t\t\t\t M.IS_DEL='0' \n" + "\t\t\t\t\t\t\t\t and\n" + "                 m.states in(1,2,3,4)\n" + "                 AND TO_CHAR (m.CREATETIME, 'yyyy') = ( \n" + "                 SELECT \n" + "                  EXTRACT (YEAR FROM SYSDATE) \n" + "                 FROM \n" + "                  dual \n" + "                 ) and ma.taskType is not null \n" + "\t\t\t\t\t\t\t\t \n" + "\t\t\t\t\t\t\t \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 COUNT (1) \n" + "                 FROM \n" + "                 MISSION M \n" + "\t\t\t\t\t\t\t\t LEFT JOIN MISSION_ASSIGN ma ON MA.missionId = M.id \n" + "                 WHERE \n" + "\t\t\t\t\t\t\t\t m.IS_DEL='0' and\n" + "                m.states in(1,2,3,4)\n" + "                 AND TO_CHAR (M .CREATETIME, 'mm') = ( \n" + "                 SELECT \n" + "                  EXTRACT (month FROM SYSDATE) \n" + "                 FROM \n" + "                  dual \n" + "                 ) and ma.taskType is not null\n" + "\t\t\t\t\t\t\t\t \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE \n" + "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" + "                 s.states in(1,2,4) and taskType = 1 and  s.source ='4' \n" + "\t\t\t\t\t\t\t\t \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE  s.IS_DEL='0' and\n" + "                 s.states in(1,2,3,4) and taskType = 1 and  s.source ='4' \n" + "\t\t\t\t\t\t\t\t \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE \n" + "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" + "                 s.states in(1,2,4) and taskType = 2 and  s.source !='4' \n" + "\t\t\t\t\t\t\t\t \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE \n" + "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" + "                 s.states in(1,2,3,4) and taskType = 2 and  s.source !='4' \n" + "\t\t\t\t\t\t\t\t \n" + "                 UNION all\n" + "\t\t\t\t\t\t\t\t\t\n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE s.IS_DEL='0' AND s.states in(1,2,4) and taskType = 1 and  s.source !='4' \n" + "                 \n" + "\t\t\t\t\t\t\t\t UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE s.IS_DEL='0' and s.states in(1,2,3,4) and taskType = 1 and  s.source !='4' \n" + "\t\t\t\t\t\t\t\t \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE \n" + "\t\t\t\t\t\t\t\t s.IS_DEL='0' and\n" + "                 s.states in(1,2,4) and taskType = 2 and  s.source ='4' \n" + "                 UNION all \n" + "\t\t\t\t\t\t\t\t \n" + "                 SELECT \n" + "                 count(1) \n" + "                 FROM \n" + "                 MISSION s \n" + "                 left join MISSION_ASSIGN ma \n" + "                 on MA.missionId = s.id \n" + "                 WHERE s.IS_DEL='0' and s.states in(1,2,3,4) and taskType = 2 and  s.source ='4' ";
        return sql;
    }




    //1、待指派；2、待处理；3、已处理  0、暂存 4、开启 5、撤销,6：忽略
    public String totalTasksJSC(String startTime, String endTime, String areaId) {
        String sql = "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m \n" + "WHERE\n" + "\tm.is_del = 0 UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m\n" + "\tLEFT JOIN mission_assign ass ON m.id = ass.missionId \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 2 \n" + "\tAND ass.DEADLINE > SYSDATE UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 1 UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 0 UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 2 UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m\n" + "\tLEFT JOIN mission_assign ass ON m.id = ass.missionId \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 2 \n" + "\tAND ass.DEADLINE < SYSDATE UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 3 \n" + "\tAND m.handleDate >= SYSDATE - 7 \n" + "\tAND m.handleDate < SYSDATE UNION ALL\n" + "SELECT\n" + "\tCOUNT( 1 ) \n" + "FROM\n" + "\tmission m \n" + "WHERE\n" + "\tm.is_del = 0 \n" + "\tAND m.states = 3 \n" + "\tAND m.handleDate >= SYSDATE - 30 \n" + "\tAND m.handleDate < SYSDATE";

        return sql;
    }

    public String responseSpeed(String startTime,String endTime) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT  DECODE(SOURCE, '1', '告警上报', '2', '人员上报', '3', '巡检异常上报', '4', '巡检计划生成', '5', '报事报修') sourceName,\n" +
                "       SUM(CASE \n" +
                "             WHEN EXTRACT(DAY FROM (m.openTime - m.assignedTime)) * 24 * 60 + EXTRACT(HOUR FROM (m.openTime - m.assignedTime)) * 60 + EXTRACT(MINUTE FROM (m.openTime - m.assignedTime)) <= 30 \n" +
                "             THEN 1 \n" +
                "             ELSE 0 \n" +
                "           END) m30,\n" +
                "       SUM(CASE \n" +
                "             WHEN EXTRACT(DAY FROM (m.openTime - m.assignedTime)) * 24 * 60 + EXTRACT(HOUR FROM (m.openTime - m.assignedTime)) * 60 + EXTRACT(MINUTE FROM (m.openTime - m.assignedTime)) BETWEEN 31 AND 60 \n" +
                "             THEN 1 \n" +
                "             ELSE 0 \n" +
                "           END) m60,\n" +
                "       SUM(CASE \n" +
                "             WHEN EXTRACT(DAY FROM (m.openTime - m.assignedTime)) * 24 * 60 + EXTRACT(HOUR FROM (m.openTime - m.assignedTime)) * 60 + EXTRACT(MINUTE FROM (m.openTime - m.assignedTime)) BETWEEN 61 AND 120 \n" +
                "             THEN 1 \n" +
                "             ELSE 0 \n" +
                "           END) m120,\n" +
                "       SUM(CASE \n" +
                "             WHEN EXTRACT(DAY FROM (m.openTime - m.assignedTime)) * 24 * 60 + EXTRACT(HOUR FROM (m.openTime - m.assignedTime)) * 60 + EXTRACT(MINUTE FROM (m.openTime - m.assignedTime)) BETWEEN 121 AND 240 \n" +
                "             THEN 1 \n" +
                "             ELSE 0 \n" +
                "           END) m240,\n" +
                "       SUM(CASE \n" +
                "             WHEN EXTRACT(DAY FROM (m.openTime - m.assignedTime)) * 24 * 60 + EXTRACT(HOUR FROM (m.openTime - m.assignedTime)) * 60 + EXTRACT(MINUTE FROM (m.openTime - m.assignedTime)) > 240 \n" +
                "             THEN 1 \n" +
                "             ELSE 0 \n" +
                "           END) m\n" +
                "FROM mission m\n" +
                "  WHERE\n" +
                "    m.is_del = 0 \n" +
                "    AND m.ASSIGNEDTIME is not null\n" +
                "    AND m.OPENTIME is not null\n" +
                "    AND m.states IN ( 2,3 ) \n" +
                "    AND m.source != 6\n" +
                "    AND  m.SOURCE is not null\n");
        if (StringUtils.isNotEmpty(startTime)) {
            builder.append(" AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'");
        }
        if (StringUtils.isNotEmpty(endTime)) {
            builder.append(" AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'");
        }
        builder.append("GROUP BY m.SOURCE");


        return builder.toString();

    }


    public static String completionStatus(MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT\n" +
                "\tDECODE( SOURCE, '1', '告警上报', '2', '人员上报', '3', '巡检异常上报', '4', '巡检计划生成', '5', '报事报修' ) sourceName,\n" +
                "\tSUM( CASE WHEN m.createTime <= m.deadline THEN 1 ELSE 0 END ) aswc,\n" +
                "\tSUM( CASE WHEN m.createTime >= m.deadline THEN 1 ELSE 0 END ) cswc,\n" +
                "\tSUM( CASE WHEN m.createTime <= m.deadline THEN 1 ELSE 0 END ) cswwc\n" +
                "FROM (\n"
        );

        String sql = pageListMission(null, bean);
        builder.append(sql);

        builder.append("   ) m\n" +
                "WHERE \n" +
                "  m.SOURCE IS NOT NULL AND m.SOURCE != 6\n");

        if (null != bean.getStates() && 2 == bean.getStates()) {
            builder.append(
                    "AND m.states IN ( 2, 4 )\n");
        }

        builder.append(
                "GROUP BY m.SOURCE\n");

        return builder.toString();

    }

    public String abnormalEquipment() {
        return "SELECT rownum,\n" + "        m.ID,\n" + "       m.ASSIGNERID,\n" + "       m.CRUNCH,\n" + "       m.CRUNCHSTATE,\n" + "       m.HANDLEDATE,\n" + "       m.HANDLEID,\n" + "       m.LEVELS,\n" + "       m.NOTES,\n" + "       m.REPORTINGTIME,\n" + "       m.SOURCE,\n" + "       m.SOURCEID,\n" + "       m.STATES,\n" + "       m.USERID,\n" + "       m.SUBSTANCE,\n" + "       m.CREATETIME,\n" + "       m.CREATEUSER,\n" + "       m.UPDATETIME,\n" + "       m.UPDATEUSER,\n" + "       m.MANAGEID,\n" + "       m.AREAID,\n" + "       m.DEVICEID,\n" + "       m.DEVICETYPE,\n" + "       m.DEVICECODE,\n" + "       m.PUBLICITY,\n" + "       m.ISREADS,\n" + "       m.TITLE,\n" + "       m.IS_DEL,\n" + "       m.BACK_REMARK,\n" + "       m.IGNORE_REMARK,\n" + "       dc.NAME deviceName,\n" + "       dc.PLACE devicePlace,\n" + "       dt.deviceTypeName\n" + "FROM MISSION m\n" + "         INNER JOIN device_aep dc ON m.deviceId = dc.id\n" + "         LEFT JOIN CONFIG_DEVICE_TYPE dt ON dt.id = dc.deviceType\n" + "WHERE m.is_del = 0\n" + "  AND m.states = 2\n";
    }


    public String missionTypeCount(String startTime,String endTime) {
        String sql = "SELECT\n" + "\tcount(DISTINCT m.id)\n" + "FROM\n" + "\tmission M\n" + "LEFT JOIN MISSION_ASSIGN ma ON MA.missionId = M . ID\n" + "where  MA.taskType = 3\n" + "and m.IS_DEL=0\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += "UNION all \n" + "SELECT\n" + "\tcount(DISTINCT m.id)\n" + "FROM\n" + "\tmission M\n" + "LEFT JOIN MISSION_ASSIGN ma ON MA.missionId = M . ID\n" + "where  MA.taskType = 4\n" + "and m.IS_DEL=0\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += "UNION all \n" + "SELECT\n" + "\tcount(DISTINCT m.id)\n" + "FROM\n" + "\tmission M\n" + "LEFT JOIN MISSION_ASSIGN ma ON MA.missionId = M . ID\n" + "where  MA.taskType = 1\n" + "and m.IS_DEL=0\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += "UNION all \n" + "SELECT\n" + "\tcount(DISTINCT m.id)\n" + "FROM\n" + "\tmission M\n" + "LEFT JOIN MISSION_ASSIGN ma ON MA.missionId = M . ID\n" + "where  MA.taskType = 2\n" + "and m.IS_DEL=0";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        return sql;
    }


    public String missionTypeCount2(String startTime,String endTime) {
        String sql = "SELECT\n" + "\tsum(case when source = '1' then 1 ELSE 0  end) count0,\n" + "\tsum(case when source = '2' then 1 ELSE 0  end) count1,\n" + "\tsum(case when source = '3' then 1 ELSE 0  end) count2,\n" + "\tsum(case when source = '4' then 1 ELSE 0  end) count3\n" + "FROM\n" + "\tmission\n" + "where IS_DEL=0";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        return sql;
    }

    public String listData() {
        String sql = "SELECT\n" + "\tm.title,m.substance,tg.name,m.reportingTime,m.handleDate,m.source,ma.taskType,m.states\n" + "FROM\n" + "\tmission m \n" + "left join MISSION_ASSIGN ma on MA.missionId = m.id\n" + "left join TEAM_GROUPS tg on tg.id = ma.teamGroupsId\n" + "where m.IS_DEL= 0 ";
        return sql;
    }

    public String listData2() {
        String sql = "SELECT\n" + "\tm.title,m.notes substance,tg.name,m.reportingTime,ma.deadline deadline,m.handleDate,m.source,ma.taskType,m.states\n" + "FROM\n" + "\tmission m \n" + "left join MISSION_ASSIGN ma on MA.missionId = m.id\n" + "left join TEAM_GROUPS tg on tg.id = ma.teamGroupsId\n" + "where m.IS_DEL= 0 and m.states=2 ";
        return sql;
    }


    public String timeData(String startTime, String endTime) {
        String sql = "SELECT\n" + "\tROUND (\n" + "\t\tMAX ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" = 4\n" +
                "and MA.taskType = 3\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tMAX ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" = 4\n" +
                "and MA.taskType = 4\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tMAX ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" != 4\n" +
                "and MA.taskType = 1\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tMAX ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" != 4\n" +
                "and MA.taskType = 2";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        return sql;
    }


    public String timeData2(String startTime, String endTime) {
        String sql = "SELECT\n" + "\tROUND (\n" + "\t\tMIN ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" = 4\n" +
                "and MA.taskType = 3\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tMIN ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" = 4\n" +
                "and MA.taskType =4\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tMIN ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" != 4\n" +
                "and MA.taskType = 1\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tMIN ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" != 4\n" +
                "and MA.taskType = 2";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        return sql;
    }


    public String timeData3(String startTime, String endTime) {
        String sql = "SELECT\n" + "\tROUND (\n" + "\t\tAVG ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" = 4\n" +
                "and MA.taskType = 3\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tAVG ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" = 4\n" +
                "and MA.taskType = 4\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tAVG ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" != 4\n" +
                "and MA.taskType = 1\n";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        sql += " UNION ALL\n" + "SELECT\n" + "\tROUND (\n" + "\t\tAVG ((m.handleDate - m.reportingTime)) * 24,\n" + "\t\t0\n" + "\t) AS count1\n" + "FROM\n" + "\tmission m\n" + "left join MISSION_ASSIGN ma on ma.missionId = m.id\n" + "where m.IS_DEL = 0\n" +
//                "and m.\"SOURCE\" != 4\n" +
                "and MA.taskType = 2";
        if (StringUtils.isNotEmpty(startTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') > '" + startTime + "'";
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql += " AND TO_CHAR (m.createTime, 'yyyy-MM-dd HH:mm:ss') < '" + endTime + "'";
        }
        return sql;
    }

    public String trendsMonth() {
        return "SELECT to_char(x.createTime, 'yyyy-MM') AS month,\n" + "       count(1)\n" + "FROM MISSION x\n" + "WHERE x.createTime IS NOT NULL\n" + "  AND to_char(x.createTime, 'yyyy') = to_char(SYSDATE, 'yyyy')\n" + "GROUP BY to_char(x.createTime, 'yyyy-MM')\n" + "ORDER BY to_char(x.createTime, 'yyyy-MM')\n";
    }

    public String trendsYear() {
        return "SELECT to_char(x.createTime, '' yyyy '') AS year,\n" + "       count(1)\n" + "FROM MISSION x\n" + "WHERE x.createTime IS NOT NULL\n" + "GROUP BY to_char(x.createTime, '' yyyy '')\n" + "ORDER BY to_char(x.createTime, '' yyyy '')\n";
    }

    public String pageReportList(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT mis.ID, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis.states,mis.title,mis.isReads, " + " ass.taskType AS assignTaskType,  ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , " + "mis.areaId,mis.createTime,mis.publicity from mission mis   left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0 and mis.source !=4 ");
        if (null != bean) {
            if (!StringUtils.isEmpty(bean.getTitle())) {
                builder.append(" AND mis.title like '%" + bean.getTitle() + "%' ");
            }
            if (!StringUtils.isNull(bean.getUserId())) {
                builder.append(" AND mis.createUser ='" + bean.getUserId() + "' ");
            }
            if (!StringUtils.isEmpty(bean.getHandleDay())) {
                builder.append(" and  TO_CHAR(mis.CREATETIME,'YYYY-MM-DD') = '" + bean.getHandleDay() + "'");
            }
        }
        builder.append(" order by mis.source,mis.states ");
        return builder.toString();
    }

    public String pageListMissionApp(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT DISTINCT a.handleStatus,a.id,a.serviceType, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a" + ".states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity," + " a.deadline, a.teamGroupsId,a.createTime FROM( " + " " +
                "SELECT mis.handleStatus,mis.ID,mis.serviceType, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis" + ".states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,mis.publicity from mission mis  " + " " +
                "left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0 ");
        if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
            builder.append(" AND mis.states IN (2, 4) and (mis.ISPENDING IS NULL OR mis.ISPENDING=0) AND ass.teamGroupsId  IN ( SELECT WM_CONCAT(DISTINCT GROUPID) AS GROUPID from team_group_user where teamRole = '1' " + " AND USERID = '" + bean.getUserId() + "' GROUP BY GROUPID) " + " UNION ALL  " + " " +
                    "SELECT mis.handleStatus,mis.id,mis.serviceType, mis.notes, mis.levels, mis.source, mis.substance, mis.userId, mis.reportingTime, mis.states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency, ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate,mis.areaId,mis.createTime,mis.publicity FROM mission mis" + " " +
                    "LEFT JOIN mission_assign ass ON mis.ID = ass.missionId " + " WHERE mis.is_del=0  AND mis.states = 2 and (mis.ISPENDING IS NULL OR mis.ISPENDING=0) AND ass.teamGroupsId IN (select WM_CONCAT(DISTINCT GROUPID) from mission_assign_user " + " where USERID = '" + bean.getUserId() + "' GROUP BY GROUPID)   ");
        } else {
            if (null != bean.getStates()) {
                if (0 != bean.getStates()) {
                    builder.append(" AND mis.states ='" + bean.getStates() + "' ");
                }
                if (3 == bean.getStates()) {//已处理数据
                    builder.append("  and mis.handleId is not null   ");
                    if (null != bean.getIsAdmin() && bean.getIsAdmin() != 1) {
                        builder.append(" AND (mis.handleId ='" + bean.getUserId() + "' or ass.CREATEUSER='" + bean.getUserId() + "')");
                    }
                }
                if (0 == bean.getStates()) {//我的上报
                    builder.append(" AND mis.createUser ='" + bean.getUserId() + "' ");
                    builder.append(" and  mis.source !=4");
                }
            }
        }
        builder.append("  ) a where 1=1 ");
        if (null != bean) {
            if (null != bean.getUrgency()) {//任务级别
                builder.append(" and a.urgency = '" + bean.getUrgency() + "'");
            }
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and a.levels = '" + bean.getLevels() + "'");
            }
            if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
                builder.append(" and a.publicity =1 ");
            }
            if (null != bean.getAssignTaskType()) {//任务类型
                builder.append(" and a.assignTaskType = '" + bean.getAssignTaskType() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and a.source = '" + bean.getSource() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and a.deadline >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and a.deadline <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getStartTime1())) {//处理时间开始时间
                builder.append(" and a.handleDate >= TO_DATE('" + bean.getStartTime1() + "','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime1())) {//处理时间结束时间
                builder.append(" and a.handleDate <= TO_DATE('" + bean.getEndTime1() + "','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getDeviceCode())) {
                builder.append(" and a.deviceCode = '" + bean.getDeviceCode() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTitle())) {
                builder.append(" AND a.title like '%" + bean.getTitle() + "%' ");
            }
            if (StringUtils.isNotEmpty(bean.getKeyword())) {
                builder.append(" AND a.title like '%" + bean.getKeyword() + "%' ");
            }
        }
//        if (null != bean.getSelectSource()) {
//            builder.append(" order by a.createTime desc");
//        } else {
//            builder.append(" order by a.source,a.states");
//        }
        builder.append(" order by a.handleDate desc");
        return builder.toString();
    }

    public String myPendingList(Page page, MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT DISTINCT a.handleStatus,a.id,a.serviceType, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a" + ".states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity," + " a.deadline, a.teamGroupsId,a.createTime,A.pendingTime,A.pendingReason,A.pendingType FROM( " + " " +
                "SELECT mis.handleStatus,mis.ID,mis.serviceType, mis.notes, mis.LEVELS, mis. SOURCE,mis.pendingTime,mis.pendingReason,mis.pendingType, mis.substance, mis.userId, mis.reportingTime,mis" + ".states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,mis.publicity from mission mis  " + " " +
                "left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0 ");
        if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
            builder.append(" AND mis.states IN (2, 4) and mis.ISPENDING=1 AND ass.teamGroupsId  IN ( SELECT WM_CONCAT(DISTINCT GROUPID) AS GROUPID from team_group_user where teamRole = '1' " + " AND USERID = '" + bean.getUserId() + "' GROUP BY GROUPID) " + " UNION ALL  " + " " +
                    "SELECT mis.handleStatus,mis.id,mis.serviceType, mis.notes, mis.levels, mis.source,mis.pendingTime,mis.pendingReason,mis.pendingType, mis.substance, mis.userId, mis.reportingTime, mis.states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency, ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate,mis.areaId,mis.createTime,mis.publicity FROM mission mis" + " " +
                    "LEFT JOIN mission_assign ass ON mis.ID = ass.missionId " + " WHERE mis.is_del=0  AND mis.states = 2 and mis.ISPENDING=1 AND ass.teamGroupsId IN (select WM_CONCAT(DISTINCT GROUPID) from mission_assign_user " + " where USERID = '" + bean.getUserId() + "' GROUP BY GROUPID)   ");
        } else {
            if (null != bean.getStates()) {
                if (0 != bean.getStates()) {
                    builder.append(" AND mis.states ='" + bean.getStates() + "' ");
                }
                if (3 == bean.getStates()) {//已处理数据
                    builder.append("  and mis.handleId is not null   ");
                    if (null != bean.getIsAdmin() && bean.getIsAdmin() != 1) {
                        builder.append(" AND (mis.handleId ='" + bean.getUserId() + "' or ass.CREATEUSER='" + bean.getUserId() + "')");
                    }
                }
                if (0 == bean.getStates()) {//我的上报
                    builder.append(" AND mis.createUser ='" + bean.getUserId() + "' ");
                    builder.append(" and  mis.source !=4");
                }
            }
        }
        builder.append("  ) a where 1=1 ");
        if (null != bean) {
            if (null != bean.getUrgency()) {//任务级别
                builder.append(" and a.urgency = '" + bean.getUrgency() + "'");
            }
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and a.levels = '" + bean.getLevels() + "'");
            }
            if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
                builder.append(" and a.publicity =1 ");
            }
            if (null != bean.getAssignTaskType()) {//任务类型
                builder.append(" and a.assignTaskType = '" + bean.getAssignTaskType() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and a.source = '" + bean.getSource() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and a.deadline >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and a.deadline <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getStartTime1())) {//处理时间开始时间
                builder.append(" and a.handleDate >= TO_DATE('" + bean.getStartTime1() + "','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime1())) {//处理时间结束时间
                builder.append(" and a.handleDate <= TO_DATE('" + bean.getEndTime1() + "','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getDeviceCode())) {
                builder.append(" and a.deviceCode = '" + bean.getDeviceCode() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTitle())) {
                builder.append(" AND a.title like '%" + bean.getTitle() + "%' ");
            }
            if (StringUtils.isNotEmpty(bean.getKeyword())) {
                builder.append(" AND a.title like '%" + bean.getKeyword() + "%' ");
            }
        }
//        if (null != bean.getSelectSource()) {
//            builder.append(" order by a.createTime desc");
//        } else {
//            builder.append(" order by a.source,a.states");
//        }
        builder.append(" order by a.handleDate desc");
        return builder.toString();
    }

    public String avgXiangYingTime() {
        return "select ROUND(AVG(ROUND(TO_NUMBER(to_date(to_char(UPDATETIME,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss') - to_date(to_char(CREATETIME,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss'))*24*60))) from MISSION WHERE IS_DEL=0 and STATES IN(3,2,4,5,6)";
    }

    public String warnHuiFu() {
        return "SELECT ROUND((SELECT COUNT(1) FROM MISSION WHERE SOURCE=1 AND IS_DEL=0 AND STATES=3 AND SOURCE=1)/(SELECT COUNT(1) FROM MISSION WHERE SOURCE=1 AND IS_DEL=0)*100) FROM dual ";
    }

    public String avgHuiFuTime() {
        return "select ROUND(AVG(ROUND(TO_NUMBER(to_date(to_char(HANDLEDATE,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss') - to_date(to_char(CREATETIME,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss'))*24*60))) from MISSION WHERE IS_DEL=0 and STATES =3 AND SOURCE=1";
    }

    public String isNotWarn() {
        return "SELECT COUNT(1) FROM MISSION WHERE SOURCE=1 AND IS_DEL=0";
    }


    public String getListByDeviceIdReport2(ElectricalSafetyReportVo2 vo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT\n" + "\ta.* ,\n" + "\t( SELECT count( 1 ) FROM DEVICE_COLLECT t WHERE t.isDel = 0 AND t.AREAID = a.AREAID ) deviceNum,\n" + "\t( SELECT count( 1 ) FROM WARNING_INFO t WHERE t.AREAID = a.AREAID ) warningNum \n" + "FROM\n" + "\t(\n" + "\tSELECT\n" + "\t\tb.areaName areaName,\n" + "\t\ta.AREAID,\n" + "\t\tc.dictLabel subitemNum,\n" + "\t\tSUM( CASE WHEN a.warnLevel = '1' THEN 1 ELSE 0 END ) AS qwNum,\n" + "\t\tSUM( CASE WHEN a.warnLevel = '2' THEN 1 ELSE 0 END ) AS ybNum,\n" + "\t\tSUM( CASE WHEN a.warnLevel = '3' THEN 1 ELSE 0 END ) AS yzNum \n" + "\tFROM\n" + "\t\tWARNING_INFO a\n" + "\t\tINNER JOIN sys_build_area b ON a.AREAID = b.id\n" + "\t\tINNER JOIN SYS_BUILD_FLOOR sbf ON b.FLOORID = sbf.ID\n" + "\t\tINNER JOIN SYS_BUILD sb ON sbf.dictBuilding = sb.ID\n" + "\t\tINNER JOIN sys_dict c ON a.warntype = c.DICTVALUE \n" + "\t\tAND c.DICTTYPE = 'warnType' \n" + "\t\tAND c.isdel = 0  \n");
        sql.append(" where 1=1 AND a.WARNTYPE IN ('18','19','20')  ");
        if (StringUtils.isNotNull(vo)) {
            if (StringUtils.isNotEmpty(vo.getStartTime())) {
                sql.append(" and to_timestamp(a.alertTime , 'yyyy-MM-dd HH24:MI:ss.ff' ) >=  to_date('").append(vo.getStartTime()).append("', 'yyyy-MM-dd HH24:MI:ss' )");
            }
            if (StringUtils.isNotEmpty(vo.getEndTime())) {
                sql.append(" and to_timestamp(a.alertTime , 'yyyy-MM-dd HH24:MI:ss.ff' ) <  to_date('").append(vo.getEndTime()).append("', 'yyyy-MM-dd HH24:MI:ss' )");
            }
            if (StringUtils.isNotNull(vo.getBuildId())) {
                sql.append(" and sb.id = ").append(vo.getBuildId());
            }
            if (StringUtils.isNotNull(vo.getFloorId())) {
                sql.append(" and b.FLOORID = ").append(vo.getFloorId());
            }
            if (StringUtils.isNotNull(vo.getAreaId())) {
                sql.append(" and a.AREAID = ").append(vo.getAreaId());
            }
            if (StringUtils.isNotEmpty(vo.getBusinessesIds())) {
                sql.append(" and a.AREAID in ").append(vo.getBusinessesIds());
            }

        }
        sql.append(" GROUP BY\n" + "\t\tb.areaName,\n" + "\t\ta.AREAID,\n" + "\t\tc.dictLabel \n" + "\tORDER BY\n" + "\t\tb.areaName,\n" + "\t\ta.AREAID,\n" + "\tc.dictLabel \n" + "\t) a");
        return sql.toString();
    }

    //小程序端-待办事项
    public String pageWorkOrderList(Page page, WorkOrderVo vo, List<String> roleFlags) {
        StringBuilder builder = new StringBuilder();

        if (null != vo.getLarge()) {
            if (0 == vo.getLarge() || 1 == vo.getLarge()) {//全部||工单事项
                if (0 == vo.getStateType() || 2 == vo.getStateType() || 4 == vo.getStateType() || 1 == vo.getStateType()) {
                    builder.append("select id,TITLE,notes,large,deadline,createUser,handleId,states,createTime,source from ( ");
                    builder.append(" select mis.id,mis.title as title,mis.notes as notes,mis.source, ");
                    builder.append(" mis.createTime,'1' as large,mis.createUser,mis.handleId,mis.states,mas.deadline as deadline from mission mis ");
                    builder.append("  LEFT JOIN mission_assign mas on mas.missionId=mis.id ");
                    builder.append(" where mis.is_del=0 and mis.source in ('3','5','6') and  mis.states not  IN ( 5,6 )  ");
                } else {
                    builder.append("select id,TITLE,notes,large,deadline,createUser,userId,states,createTime,source from ( ");
                    builder.append(" select mis.id,mis.title as title,mis.notes as notes,ass.userId as userId,mis.source, ");
                    builder.append(" mis.createTime,'1' as large,mis.createUser,mis.states,mas.deadline as deadline from mission mis left join mission_assign_user ");
                    builder.append(" ass on mis.id = ass.missionId LEFT JOIN mission_assign mas on mas.missionId=mis.id ");
                    builder.append(" where mis.is_del=0 and mis.source in ('3','5','6') and  mis.states not  IN ( 5,6 )  ");
                }

            }
            if (null != vo.getSmall()) {//小分类
                builder.append(" and mis.source ='" + vo.getSmall() + "' ");
            }
            //后期拓展
//            if (0 == vo.getLarge()) {
//                builder.append("UNION all");
//            }
//            if (0 == vo.getLarge() || 2 == vo.getLarge()) {//全部||访客预约
//
//            }
        }
        builder.append(" ) a where 1=1  ");
        //状态类型（默认0）0、全部 1、进行中 2、我发起的  3、指派给我的  4、已完成的
        if (null != vo.getStateType()) {//状态分类
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                //普通用户
                if (0 == vo.getStateType()) {
                    builder.append(" and (a.createUser ='" + vo.getUserId() + "' )");

                } else if (4 == vo.getStateType()) {
                    builder.append(" and (a.handleId ='" + vo.getUserId() + "' )");
                }
            } else {
                //管理员
                if ((2 == vo.getStateType() || 3 == vo.getStateType())) {//2、我发起的  3、指派给我的
                    builder.append(" and (a.createUser ='" + vo.getUserId() + "' )");
                }
            }

            if (1 == vo.getStateType()) {//待指派
                builder.append(" and a.states in (1) ");
            }
            if (5 == vo.getStateType()) {//待处理
                builder.append(" and a.states in (2,4) ");
            }
            if (2 == vo.getStateType() && null != vo.getUserId()) {//我发起的
                builder.append(" and a.createUser = '" + vo.getUserId() + "' ");
            }
            if (3 == vo.getStateType() && null != vo.getUserId()) {//指派给我的
                builder.append(" and a.userId = '" + vo.getUserId() + "' ");
            }
            if (4 == vo.getStateType()) {//已完成的
                builder.append(" and a.states = '3' ");
            }
        }
        if (null != vo.getSortType()) {//排序分类
            if (0 == vo.getSortType()) {//默认
                builder.append(" order by a.large");
            }
            if (1 == vo.getSortType()) {//创建时间近到远
                builder.append(" order by a.createTime desc");
            }
            if (2 == vo.getSortType()) {//创建时间远到近
                builder.append(" order by a.createTime asc");
            }
            if (3 == vo.getSortType()) {//截止时间近到远
                builder.append(" order by a.deadline desc");
            }
            if (4 == vo.getSortType()) {//截止时间远到近
                builder.append(" order by a.deadline asc ");
            }
        }
        return builder.toString();
    }


    //小程序-统计
    public String workSourceCount(Integer large, Integer small, Integer userId, List<String> roleFlags) {
        String sql = "SELECT  count(1)  FROM  mission mis LEFT JOIN mission_assign_user ass ON mis. ID = ass.missionId  " + " LEFT JOIN mission_assign mas ON mas.missionId = mis.id WHERE mis.is_del = 0 ";
        if (null != small) {
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                //普通用户
                sql += " and (mis.createUser ='" + userId + "' or mis.userId='" + userId + "')   ";
            }
            sql += "  and mis.states in (1) and mis.source ='" + small + "' ";
        } else {
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                sql += " and (mis.createUser ='" + userId + "' or mis.userId='" + userId + "')  ";
            }

            sql += " and mis.states in (1) and mis.source in ('3','5','6')";
        }
        sql += "UNION ALL ";

        sql += "SELECT  count(1)  FROM  mission mis   " + " LEFT JOIN mission_assign mas ON mas.missionId = mis.id WHERE mis.is_del = 0 ";
        if (null != small) {

            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                sql += "and mis.createUser ='" + userId + "' ";
            }

            sql += "  and mis.source ='" + small + "'";
        } else {
            if (null != userId) {
                sql += "and mis.createUser ='" + userId + "'  ";
            }
            sql += " and mis.source in ('3','5','6') ";
        }
        sql += "UNION ALL ";
        sql += "SELECT  count(1)  FROM  mission mis LEFT JOIN mission_assign_user ass ON mis. ID = ass.missionId  " + " LEFT JOIN mission_assign mas ON mas.missionId = mis.id WHERE mis.is_del = 0 ";
        if (null != small) {
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                sql += "and mis.assignerId ='" + userId + "' ";
            }

            sql += " and mis.source ='" + small + "' ";
        } else {
            if (null != userId) {
                sql += "and ass.userId ='" + userId + "' ";
            }
            sql += " and mis.source in ('3','5','6') ";
        }
        sql += "UNION ALL ";
        sql += "SELECT  count(1)  FROM  mission mis   " + " LEFT JOIN mission_assign mas ON mas.missionId = mis.id WHERE mis.is_del = 0 ";
        if (null != small) {

            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                sql += "and  (mis.createUser ='" + userId + "' or mis.userId='" + userId + "') ";
            }

            sql += " and mis.states = '3' and mis.source ='" + small + "' ";
        } else {
            //普通用户
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                sql += " and  (mis.createUser ='" + userId + "' or mis.userId='" + userId + "') ";
                sql += " and  mis.handleId ='" + userId + "' ";
            }

            sql += " and  mis.states = '3' and mis.source in ('3','5','6') ";
        }
        sql += "UNION ALL ";
        sql += "SELECT  count(1)  FROM  mission mis LEFT JOIN mission_assign_user ass ON mis. ID = ass.missionId  " + " LEFT JOIN mission_assign mas ON mas.missionId = mis.id WHERE mis.is_del = 0 ";
        if (null != small) {
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                //普通用户
                sql += " and (mis.createUser ='" + userId + "' or mis.userId='" + userId + "')   ";
            }
            sql += "  and mis.states in (2,4) and mis.source ='" + small + "' ";
        } else {
            if (!roleFlags.contains("yyadmin") && !roleFlags.contains("zhyyadmin")) {
                sql += " and (mis.createUser ='" + userId + "' or mis.userId='" + userId + "')  ";
            }
            sql += " and mis.states in (2,4) and mis.source in ('3','5','6')";
        }

        return sql;
    }


    //工单类型统计
    public String missionType() {
        StringBuilder str = new StringBuilder();
        str.append("SELECT\n" + "\ta.DICTLABEL,\n" + "\tNVL(b.POINT, '0') POINT\n" + "FROM\n" + "\t( SELECT * FROM SYS_DICT c WHERE c.DICTTYPE = 'taskTypes' ) a\n" + "\tLEFT JOIN (\n" + "\tSELECT\n" + "\t\tb.TASKTYPE,\n" + "\t\tCOUNT( 1 ) POINT \n" + "\tFROM\n" + "\t\tMISSION a\n" + "\t\tLEFT JOIN MISSION_ASSIGN b ON a.ID = b.MISSIONID \n" + "\tWHERE\n" + "\t\t1 = 1 \n" + "\t\tAND b.TASKTYPE IS NOT NULL \n" + "\t\tAND a.IS_DEL = 0 \n" + "\tGROUP BY\n" + "\t\tb.TASKTYPE \n" + "\t) b ON a.DICTVALUE = b.TASKTYPE \n" + "WHERE\n" + "\t1 = 1 \n" + "\tAND a.ISDEL = 0 ORDER BY NVL(b.POINT, '0') DESC");
        return str.toString();
    }

    //工单来源统计
    public String missionSource() {
        StringBuilder str = new StringBuilder();
        str.append("SELECT\n" + "\t(\n" + "\tCASE\n" + "\t\t\t\n" + "\t\t\tWHEN a.datevalue = 1 THEN\n" + "\t\t\t'告警上报' \n" + "\t\t\tWHEN a.datevalue = 2 THEN\n" + "\t\t\t'人员上报' \n" + "\t\t\tWHEN a.datevalue = 3 THEN\n" + "\t\t\t'巡检异常上报' \n" + "\t\t\tWHEN a.datevalue = 4 THEN\n" + "\t\t\t'巡检计划生成' \n" + "\t\t\tWHEN a.datevalue = 5 THEN\n" + "\t\t\t'报事报修' \n" + "\t\t\tWHEN a.datevalue = 6 THEN\n" + "\t\t\t'投诉申请' \n" + "\t\tEND \n" + "\t\t) AS SOURCE,\n" + "\t\tNVL(b.POINT, 0) POINT\n" + "\tFROM\n" + "\t\t( SELECT lpad( LEVEL, 1, 0 ) datevalue FROM dual CONNECT BY LEVEL < 7 ) a\n" + "\t\tLEFT JOIN (\n" + "\t\tSELECT\n" + "\t\t\ta.SOURCE,\n" + "\t\t\tCOUNT( 1 ) AS POINT \n" + "\t\tFROM\n" + "\t\t\tMISSION a\n" + "\t\t\tLEFT JOIN MISSION_ASSIGN b ON a.ID = b.MISSIONID \n" + "\t\tWHERE\n" + "\t\t\t1 = 1 \n" + "\t\t\tAND a.IS_DEL = 0 \n" + "\t\tGROUP BY\n" + "\t\t\ta.SOURCE \n" + "\t\t) b ON a.DATEVALUE = b.SOURCE ORDER BY NVL(b.POINT, 0) DESC");
        return str.toString();
    }


    //工单趋势今年
    public String missionTrend() {
        StringBuilder str = new StringBuilder();
        str.append("\tSELECT\n" + "\t\ta.DATEVALUE,\n" + "\t\tNVL(b.POINT, '0') POINT\n" + "\tFROM\n" + "\t\t( SELECT to_char( SYSDATE, 'yyyy-' ) || lpad( LEVEL, 2, 0 ) datevalue FROM dual CONNECT BY LEVEL" + " < 13 ) a\n" + "\t\tLEFT JOIN (\n" + "\t\tSELECT\n" + "\t\t\tTO_CHAR( REPORTINGTIME, 'yyyy-MM' ) AS datevalue,\n" + "\t\t\tCOUNT( 1 ) AS POINT \n" + "\t\tFROM\n" + "\t\t\tMISSION \n" + "\t\tWHERE\n" + "\t\t\t1 = 1 \n" + "\t\t\tAND IS_DEL = 0 \n" + "\t\tGROUP BY\n" + "\t\t\tTO_CHAR( REPORTINGTIME, 'yyyy-MM' ) \n" + "\t\t) b ON a.datevalue = b.datevalue ORDER BY a.DATEVALUE ");
        return str.toString();
    }

    //工单趋势去年
    public String missionTrendLast() {
        StringBuilder str = new StringBuilder();
        str.append("SELECT\n" + "\t\ta.DATEVALUE,\n" + "\t\tNVL(b.POINT, 0) POINT\n" + "\tFROM\n" + "\t\t(\n" + "\t\tSELECT\n" + "\t\t\tto_char( ADD_MONTHS( TRUNC( SYSDATE, 'y' ), - 12 ), 'yyyy-' ) || lpad( LEVEL, 2, 0 ) datevalue" + " \n" + "\t\tFROM\n" + "\t\t\tdual CONNECT BY LEVEL < 13 \n" + "\t\t) a\n" + "\t\tLEFT JOIN (\n" + "\t\tSELECT\n" + "\t\t\tTO_CHAR( REPORTINGTIME, 'yyyy-MM' ) AS datevalue,\n" + "\t\t\tCOUNT( 1 ) AS POINT \n" + "\t\tFROM\n" + "\t\t\tMISSION \n" + "\t\tWHERE\n" + "\t\t\t1 = 1 \n" + "\t\t\tAND IS_DEL = 0 \n" + "\t\tGROUP BY\n" + "\t\t\tTO_CHAR( REPORTINGTIME, 'yyyy-MM' ) \n" + "\t\t) b ON a.datevalue = b.datevalue  ORDER BY a.DATEVALUE");
        return str.toString();
    }

    public String missionTodayCount(String deptId) {
        String sql = "SELECT\n" + "\tcount(1) ccc\n" + "FROM\n" + "\tMISSION M\n" +
//                "LEFT JOIN sys_dept_area da ON DA.AREAID = M .AREAID\n" +
                "WHERE\n" + "\tM .IS_DEL = 0\n" + "AND M .reportingTime >= SYSDATE -1\n AND M .reportingTime < SYSDATE";
//        if(StringUtils.isNotEmpty(deptId)){
//            sql += " AND da.deptid = "+deptId+"";
//        }
        return sql;
    }

    public String missionCount(String deptId) {
        String sql = "SELECT \n" + "\tcount(1) count\n" + "FROM\n" + "\tMISSION M\n" +
//                "LEFT JOIN sys_dept_area da ON DA.AREAID = M .AREAID\n" +
                "WHERE\n" + "\t(M .states = 1 or M .states = 2 or M .states = 4 )\n" + "AND is_del = 0\n";
//        if(StringUtils.isNotEmpty(deptId)){
//            sql += " AND da.deptid = "+deptId+"";
//        }
        return sql.toString();
    }

    public String missionTimeCount(Integer day, String userId) {
        String sql = "SELECT\n" + "\tcount(1) ccc\n" + "FROM\n" + "\tMISSION M\n" +
//                "LEFT JOIN sys_dept_area da ON DA.AREAID = M .AREAID\n" +
//                "LEFT JOIN SYS_USER su ON su.deptId = da.deptId\n" +
                "WHERE\n" + "\tM .IS_DEL = 0\n" + "AND M .STATES = 3\n" + "AND M .HANDLEDATE >= SYSDATE - \n" + day + "AND M .HANDLEDATE < SYSDATE\n";
//        if(StringUtils.isNotEmpty(userId)){
//            sql += " AND su.ID = "+userId+"";
//        }
        return sql.toString();
    }

    public String overdueMissionCount(String userId) {
        String sql = "SELECT\n" + "\tCOUNT (1) ccc\n" + "FROM\n" + "\tMISSION M\n" + "LEFT JOIN MISSION_ASSIGN ma ON M .\"ID\" = MA.MISSIONID\n" +
//                "LEFT JOIN sys_dept_area da ON DA.AREAID = M .AREAID\n" +
//                "LEFT JOIN SYS_USER su ON su.deptId = da.deptId\n" +
                "AND MA.STATE = 1\n" + "WHERE\n" + "\tM .IS_DEL = 0\n" + "AND (M .states = 2 OR M .states = 4)\n" + "AND MA.DEADLINE < SYSDATE\n";
//        if(StringUtils.isNotEmpty(userId)){
//            sql += " AND su.ID = "+userId+"";
//        }
        return sql;
    }

    public String undispatchedOrderCpount(String states, String userId) {
        String sql = "SELECT\n" + "\tcount(1) count\n" + "FROM\n" + "\tMISSION M\n" +
//                "LEFT JOIN sys_dept_area da ON DA.AREAID = M .AREAID\n" +
//                "LEFT JOIN SYS_USER su ON su.deptId = da.deptId\n" +
                "WHERE\n" + "\tM .states = " + states + "\n" + "AND is_del = 0\n";

//        if(StringUtils.isNotEmpty(userId)){
//            sql += " AND su.ID = "+userId+"";
//        }

        return sql;
    }


    public String pageListMissionNoPage(MissionVo bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT DISTINCT a.id, a.notes, a.levels, a.source, a.substance, a.userId, a.reportingTime, a" + ".states,a.title,a.isReads, a.assignTaskType, a.urgency,a.handleDate,a.areaId ,a.publicity," + " a.deadline, a.teamGroupsId,a.createTime FROM( " + " SELECT mis.ID, mis.notes, mis.LEVELS, mis. SOURCE, mis.substance, mis.userId, mis.reportingTime,mis" + ".states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency,ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate , mis.areaId,mis.createTime,mis.publicity from mission mis  " + " left join mission_assign ass ON mis.id = ass.missionId where mis.is_del=0 ");
        if (null != bean.getIsHandle() && 2 == bean.getIsHandle()) {//待处理功能
            if (null != bean.getHome_tab_type()) {
                if (1 == bean.getHome_tab_type()) {//逾期
                    builder.append(" and ass.DEADLINE<SYSDATE ");
                }
            }
            builder.append(" AND mis.states IN (2, 4) AND ass.teamGroupsId  IN ( SELECT WM_CONCAT(DISTINCT GROUPID) AS GROUPID from team_group_user where teamRole = '1' " + " AND USERID = '" + bean.getUserId() + "' GROUP BY GROUPID) " + " UNION ALL  " + " SELECT mis.id, mis.notes, mis.levels, mis.source, mis.substance, mis.userId, mis.reportingTime, mis.states,mis.title,mis.isReads, ass.taskType AS assignTaskType, " + " ass.urgency AS urgency, ass.deadline AS deadline, ass.teamGroupsId AS teamGroupsId , mis.handleDate,mis.areaId,mis.createTime,mis.publicity FROM mission mis" + " LEFT JOIN mission_assign ass ON mis.ID = ass.missionId " + " WHERE mis.is_del=0  AND mis.states = 2  ");
            if (null != bean.getHome_tab_type()) {
                if (1 == bean.getHome_tab_type()) {//逾期
                    builder.append(" and ass.DEADLINE<SYSDATE ");
                }
            }
            builder.append(" AND ass.teamGroupsId IN (select WM_CONCAT(DISTINCT GROUPID) from mission_assign_user " + " where USERID = '" + bean.getUserId() + "' GROUP BY GROUPID) ");
        } else {
            if (null != bean.getStates()) {
                if (0 != bean.getStates()) {
                    builder.append(" AND mis.states ='" + bean.getStates() + "' ");
                }
                if (3 == bean.getStates()) {//已处理数据
                    builder.append("  and mis.handleId is not null   ");
                    if (null != bean.getIsAdmin() && bean.getIsAdmin() != 1) {
                        builder.append(" AND (mis.handleId ='" + bean.getUserId() + "' or ass.CREATEUSER='" + bean.getUserId() + "')");
                    }

                    if (null != bean.getHome_tab_type()) {
                        if (2 == bean.getHome_tab_type()) {//7天
                            builder.append(" and  mis.handleDate>=SYSDATE-7 AND mis.handleDate<sysdate ");
                        }
                        if (3 == bean.getHome_tab_type()) {//30天
                            builder.append(" and  mis.handleDate>=SYSDATE-30 AND mis.handleDate<sysdate ");
                        }
                    }
                }
                if (0 == bean.getStates()) {//我的上报
                    builder.append(" AND mis.createUser ='" + bean.getUserId() + "' ");
                    builder.append(" and  mis.source !=4");
                }
            }
        }
        builder.append("  ) a where 1=1 ");
        if (null != bean) {
            if (null != bean.getUrgency()) {//任务级别
                builder.append(" and a.urgency = '" + bean.getUrgency() + "'");
            }
            if (null != bean.getLevels()) {//任务级别
                builder.append(" and a.levels = '" + bean.getLevels() + "'");
            }
            if (null != bean.getPublicity() && 1 == bean.getPublicity()) {
                builder.append(" and a.publicity =1 ");
            }
            if (null != bean.getAssignTaskType()) {
                if (null != bean.getAssignTask()) {
                    if ("planType".equals(bean.getAssignTask())) {
                        builder.append(" and a.source =4 ");
                    } else {
                        builder.append(" and a.source !=4 ");
                    }
                }
                builder.append(" and a.assignTaskType = '" + bean.getAssignTaskType() + "'");
            }
            if (null != bean.getSource()) {
                builder.append(" and a.source = '" + bean.getSource() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getStartTime())) {//处理开始时间
                builder.append(" and a.deadline >= TO_DATE('" + bean.getStartTime() + " 00:00:00','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getEndTime())) {//处理结束时间
                builder.append(" and a.deadline <= TO_DATE('" + bean.getEndTime() + " 23:59:59','YYYY-MM-DD hh24:mi:ss')");
            }
            if (StringUtils.isNotEmpty(bean.getDeviceCode())) {
                builder.append(" and a.deviceCode = '" + bean.getDeviceCode() + "'");
            }
            if (StringUtils.isNotEmpty(bean.getTitle())) {
                builder.append(" AND a.title like '%" + bean.getTitle() + "%' ");
            }
        }
        if (null != bean.getSelectSource()) {
            builder.append(" order by a.createTime desc");
        } else {
            builder.append(" order by a.source,a.states");
        }
        return builder.toString();
    }


    public String missionCount2(String startTime, String endTime) {
        String sql = "SELECT\n" + "\tcount(1) ccc\n" + "FROM\n" + "\tMISSION M\n" + "WHERE\n" + "\tM .IS_DEL = 0\n";
        if (StringUtils.isNotEmpty(startTime)) {//处理开始时间
            sql += " AND M .reportingTime >= TO_DATE('" + startTime + "','YYYY-MM-DD hh24:mi:ss')";
        }
        if (StringUtils.isNotEmpty(endTime)) {//处理结束时间
            sql += " AND M .reportingTime <= TO_DATE('" + endTime + "','YYYY-MM-DD hh24:mi:ss')";
        }
        return sql;
    }

    public String alarmAvgXiangYingTime(AlarmStatisticsVo vo) {
        String sql =  "select ROUND(AVG(ROUND(TO_NUMBER(to_date(to_char(UPDATETIME,'yyyy-MM-dd hh24:mi:ss')," +
                "'yyyy-MM-dd" +
                " " +
            "hh24:mi:ss') - to_date(to_char(CREATETIME,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss'))*24*60))) " +
                "from MISSION WHERE IS_DEL=0 and STATES IN(3,2,4,5,6) AND SOURCE=1";
        sql = handleTime(sql,vo);
        return sql;
    }

    public String alarmHuiFu(AlarmStatisticsVo vo) {
        String sql = "SELECT ROUND((SELECT COUNT(1) FROM MISSION WHERE SOURCE=1 AND IS_DEL=0 AND STATES=3 AND " +
                "SOURCE=1";
        sql = handleTime(sql,vo);
        sql+= ")/(SELECT COUNT(1) FROM MISSION WHERE SOURCE=1 AND IS_DEL=0 ";
        sql = handleTime(sql,vo);
        sql+=")*100) FROM dual ";

        return sql;
    }


    public String alarmAvgHuiFuTime(AlarmStatisticsVo vo) {
        String sql = "select ROUND(AVG(ROUND(TO_NUMBER(to_date(to_char(HANDLEDATE,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss') - to_date(to_char(CREATETIME,'yyyy-MM-dd hh24:mi:ss'),'yyyy-MM-dd hh24:mi:ss'))*24*60))) from MISSION WHERE IS_DEL=0 and STATES =3 AND SOURCE=1";
        sql = handleTime(sql,vo);
        return sql;
    }

    public String alarmIsNotWarn(AlarmStatisticsVo vo) {
        String sql = "SELECT COUNT(1) FROM MISSION WHERE SOURCE=1 AND IS_DEL=0 AND SOURCE=1 ";
        sql = handleTime(sql,vo);
        return sql;
    }

    public String handleTime(String sql, AlarmStatisticsVo vo){
        if (Objects.equals(vo.getRadio(),"1")){
            sql+= "    AND CREATETIME >= TO_DATE('"+vo.getStartTime()+"', 'yyyy-MM-dd hh24:mi:ss') \n" +
                    "    AND CREATETIME <= TO_DATE('"+vo.getEndTime()+"', 'yyyy-MM-dd hh24:mi:ss') ";
        }else if (Objects.equals(vo.getRadio(),"2")){
            sql+= "    AND CREATETIME  >= TO_DATE('"+ vo.getStartTime()+"', 'yyyy-MM-dd hh24:mi:ss') "+
                    "    AND CREATETIME <= TO_DATE('"+vo.getEndTime()+"', 'yyyy-MM-dd hh24:mi:ss') ";
        }else if (Objects.equals(vo.getRadio(),"3")){
            sql+= "    AND CREATETIME  >= TO_DATE('"+ vo.getStartTime()+"', 'yyyy-MM-dd hh24:mi:ss') "+
                    "    AND CREATETIME <= TO_DATE('"+vo.getEndTime()+"', 'yyyy-MM-dd hh24:mi:ss') ";
        }else if (Objects.equals(vo.getRadio(),"4")){
            sql+= "    AND CREATETIME >= TO_DATE('"+vo.getStartTime()+"' , 'yyyy-MM-dd hh24:mi:ss') \n" +
                    "    AND CREATETIME <= TO_DATE('"+vo.getEndTime()+"', 'yyyy-MM-dd hh24:mi:ss') ";
        }else {
            sql+="";
        }
        return sql;
    }
}
