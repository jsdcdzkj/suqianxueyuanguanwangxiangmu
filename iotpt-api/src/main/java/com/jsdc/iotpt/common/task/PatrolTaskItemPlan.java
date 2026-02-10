package com.jsdc.iotpt.common.task;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.model.PatrolTimes;
import com.jsdc.iotpt.service.PatrolPlanService;
import com.jsdc.iotpt.service.PatrolTaskService;
import com.jsdc.iotpt.service.PatrolTimesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * ClassName: PatrolTaskItemPlan
 * Description: 巡更计划子项
 * date: 2024/1/16 14:58
 *
 * @author bn
 */
public class PatrolTaskItemPlan implements Task {

    private static final Logger log = LoggerFactory.getLogger(PatrolTaskItemPlan.class);

    private String planId;

    private String type;

    @Autowired
    private PatrolTimesService timesService;
    @Autowired
    private PatrolPlanService patrolPlanService;
    @Autowired
    private PatrolTaskService patrolTaskService;

    public PatrolTaskItemPlan(String type, String planId, PatrolTimesService patrolTimesService, PatrolPlanService patrolPlanService, PatrolTaskService patrolTaskService) {
        this.type = type;
        this.planId = planId;
        this.timesService = patrolTimesService;
        this.patrolPlanService = patrolPlanService;
        this.patrolTaskService = patrolTaskService;
    }

    @Override
    public void execute() {
        PatrolTimes patrolTimes = timesService.getById(planId);
        log.info("=========巡更计划子项==================planId:"+planId+"======type:"+type);
        if (1 == patrolTimes.getIsDel()) {
            CronUtil.remove(type + planId);
        }

        PatrolPlan patrolPlan = patrolPlanService.getById(patrolTimes.getPlanId());

        if (1 == patrolPlan.getIsDel()) {
            CronUtil.remove(type + planId);
        }

        if (1==patrolPlan.getPlanStatus()){
            return;
        }



        PatrolTask patrolTask=new PatrolTask();
        patrolTask.setIsDel(0);
        patrolTask.setPlanId(String.valueOf(patrolPlan.getId()));
        patrolTask.setClockInCount(patrolPlan.getClockInCount());
        patrolTask.setTaskType(patrolPlan.getPlanType());
        patrolTask.setTaskStatus("0");
        Date date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        patrolTask.setCycleStartTime(dateFormat.format(date)+" "+patrolTimes.getCycleStartTime()+":00");
        patrolTask.setCycleEndTime(dateFormat.format(date)+" "+patrolTimes.getCycleEndTime()+":00");
        patrolTask.setCreateTime(new Date());
        try {
            List<PatrolTask> patrolTasks=patrolTaskService.list(Wrappers.<PatrolTask>lambdaQuery().
                    eq(PatrolTask::getIsDel,0).
                    eq(PatrolTask::getPlanId,String.valueOf(patrolPlan.getId())).
                    eq(PatrolTask::getCycleStartTime,dateFormat.format(date)+" "+patrolTimes.getCycleStartTime()+":00").
                    eq(PatrolTask::getCycleEndTime,dateFormat.format(date)+" "+patrolTimes.getCycleEndTime()+":00"));
            if (!patrolTasks.isEmpty()){
                log.info("=========巡更计划子项=========patrolTasks:true");
                return;
            }

            String msg=String.format("ip地址：%s,编号：%s",InetAddress.getLocalHost().getHostAddress(),type + planId);
            log.info("=========巡更计划子项=========msg:"+msg);
            patrolTask.setMsg(msg);
            Thread.sleep(new Random().nextInt(1000) + 2000);
            patrolTaskService.getBaseMapper().insert(patrolTask);
        }catch (Exception e){

        }





    }
}
