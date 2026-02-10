package com.jsdc.iotpt.common.task;

import cn.hutool.Hutool;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.service.PatrolPlanService;
import com.jsdc.iotpt.service.PatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;

/**
 * ClassName: 单次巡更计划
 * Description:
 * date: 2024/1/12 8:55
 *
 * @author bn
 */
public class PatrolTaskPlan implements Task {

    private String planId;

    private String type;

    @Autowired
    private PatrolPlanService patrolPlanService;
    @Autowired
    private PatrolTaskService patrolTaskService;



    public PatrolTaskPlan(String type,String planId, PatrolPlanService patrolPlanService, PatrolTaskService patrolTaskService) {
        this.type=type;
        this.planId = planId;
        this.patrolPlanService = patrolPlanService;
        this.patrolTaskService=patrolTaskService;
    }


    @Override
    public void execute() {

        PatrolPlan plan=patrolPlanService.getById(planId);

        // 移除任务
        CronUtil.remove(type+planId);

        if (1==plan.getIsDel()){
            return;
        }

        if(1==plan.getPlanStatus()){
            return;
        }

        PatrolTask patrolTask=new PatrolTask();
        patrolTask.setIsDel(0);
        patrolTask.setPlanId(String.valueOf(plan.getId()));
        patrolTask.setClockInCount(plan.getClockInCount());
        patrolTask.setTaskType(plan.getPlanType());
        patrolTask.setTaskStatus("0");
        patrolTask.setClockNum(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        patrolTask.setCycleStartTime(dateFormat.format(plan.getCycleStartTime()));
        patrolTask.setCycleEndTime(dateFormat.format(plan.getCycleEndTime()));

        patrolTaskService.getBaseMapper().insert(patrolTask);

    }
}
