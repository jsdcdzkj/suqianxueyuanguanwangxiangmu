package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.PatrolTaskVo;
import org.springframework.stereotype.Repository;


@Repository
public class PatrolTaskDao {
    public String getPatrolTaskList(Page page, PatrolTaskVo patrolTaskVo) {
        String sql = "SELECT DISTINCT Pt.id,po.userId,\n" +
                "\tpp.planName,Pt.taskType,pp.planRemark,pt.createTime,PT.taskStatus,SU.realName,pt.cycleStartTime,pt.cycleEndTime\n" +
                "FROM\n" +
                "\tPatrolTask pt\n" +
                "LEFT JOIN PATROL_PLAN pp ON pt.planId = PP. ID\n" +
                "LEFT JOIN PATROL_PEOPLE po ON po.planId = pp. ID\n" +
                "LEFT JOIN sys_user su ON SU. ID = po.userId\n" +
                "WHERE pt.isDel=0\n"  ;

        //任务计划模糊查询
        if (StringUtils.isNotEmpty(patrolTaskVo.getTaskName())) {
            sql += " and pp.planName LIKE '%"+patrolTaskVo.getTaskName()+"%'" ;
        }
        //任务类型查询
        if (StringUtils.isNotEmpty(patrolTaskVo.getTaskType())) {
            sql += " and pp.planType = '"+patrolTaskVo.getTaskType()+"'" ;
        }
        //权限
        if (null != patrolTaskVo.getUserId()) {
            sql += " and po.userId = '"+patrolTaskVo.getUserId()+"'" ;
        }
        //状态
        if (StringUtils.isNotEmpty(patrolTaskVo.getTaskStatus())) {
            sql += " and pt.taskStatus = '"+patrolTaskVo.getTaskStatus()+"'" ;
        }
        sql +=" order by pt.taskStatus asc,pt.cycleStartTime desc" ;
        return sql;
    }

    public static String getVideo(String id) {
        String sql = "SELECT pt.ID,\n" +
                "       pt.TASKTIME,\n" +
                "       pt.cycleStartTime,\n" +
                "       pt.cycleEndTime,\n" +
                "       pt.PLANID,\n" +
                "       pp.CLOCKINCOUNT,\n" +
                "       pp.CYCLEDATE,\n" +
                "       pp.CYCLETIME,\n" +
                "       pp.EXECUTETYPE,\n" +
                "       pp.PLANCYCLE,\n" +
                "       pp.PLANNAME,\n" +
                "       pp.PLANREMARK,\n" +
                "       pp.PLANSTATUS,\n" +
                "       pp.PLANTYPE,\n" +
                "       pd.DEVICEID,\n" +
                "       pd.DURATION,\n" +
                "       dc.id deviceId,\n" +
                "       dc.NAME,\n" +
                "       dc.floorId,\n" +
                "       dc.buildId,\n" +
                "       dc.areaId\n" +
                "FROM PATROLTASK pt\n" +
                "         LEFT JOIN PATROL_PLAN pp ON pt.PLANID = pp.ID\n" +
                "         LEFT JOIN PATROL_DEVICE_OR_POINT pd ON pp.ID = pd.planId and pd.isDel = 0\n" +
                "         LEFT JOIN DEVICE_VIDEO dc ON pd.DEVICEID = dc.ID\n" +
                "WHEre pt.id = " + id + " \n";
        return sql;
    }

    public static void main(String[] args) {
        System.out.println(getVideo("1"));
    }

    public String getPoint(String id) {
        String sql = "SELECT DISTINCT pt.ID,\n" +
                "       pt.TASKTIME,\n" +
                "       pt.PLANID,\n" +
                "       pp.CLOCKINCOUNT,\n" +
                "       pp.CYCLEDATE,\n" +
                "       pp.CYCLETIME,\n" +
                "       pp.EXECUTETYPE,\n" +
                "       pp.PLANCYCLE,\n" +
                "       pp.PLANNAME,\n" +
                "       pp.PLANREMARK,\n" +
                "       pp.PLANSTATUS,\n" +
                "       pp.PLANTYPE,\n" +
                "       pd.DEVICEID,\n" +
                "       pd.DURATION,\n" +
                "       p.name,\n" +
                "       p.remarks,\n" +
                "       p.areaNames,\n" +
                "       SU.realName\n" +
                "FROM PATROLTASK pt\n" +
                "         LEFT JOIN PATROL_PLAN pp ON pt.PLANID = pp.ID\n" +
                "         LEFT JOIN PATROL_PEOPLE po ON po.planId = pp. ID\n" +
                "         LEFT JOIN sys_user su ON SU. ID = po.userId\n" +
                "         LEFT JOIN PATROL_DEVICE_OR_POINT pd ON pp.ID = pd.PLANID\n" +
                "         LEFT JOIN POINT p ON pd.POINTID = p.ID\n" +
                "WHERE pt.id = " + id + " \n";
        return sql;
    }

}
