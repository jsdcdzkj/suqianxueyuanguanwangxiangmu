package com.jsdc.iotpt.common.task;

import cn.hutool.cron.CronUtil;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: TaskManager
 * Description:
 * date: 2023/8/22 15:56
 *
 * @author bn
 */
@Component
public class TaskManager {

    @Autowired
    private JobPlanService jobPlanService;

    @Autowired
    private PatrolPlanService patrolPlanService;

    @Autowired
    private PatrolTimesService patrolTimesService;

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private MissionItemRecordService itemRecordService;


    public void createTask(String type, String planId, String cronExpression) {

        if (type.equals(G.JOB_PLAN)) {
            PlanTask task = new PlanTask(type, planId, jobPlanService, itemRecordService);
            CronUtil.schedule(type + planId, cronExpression, task);
        } else if (type.equals(G.PATROL_PLAN)) {
            PatrolTaskPlan patrolTask = new PatrolTaskPlan(type, planId, patrolPlanService, patrolTaskService);
            CronUtil.schedule(type + planId, cronExpression, patrolTask);
        } else if (type.equals(G.PATROL_TIMES)) {
            PatrolTaskItemPlan itemPlan = new PatrolTaskItemPlan(type, planId, patrolTimesService, patrolPlanService, patrolTaskService);
            CronUtil.schedule(type + planId, cronExpression, itemPlan);
        }


        if (!CronUtil.getScheduler().isStarted()) {
            CronUtil.start();
        }
    }



}
