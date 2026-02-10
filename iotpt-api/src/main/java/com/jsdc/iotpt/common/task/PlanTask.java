package com.jsdc.iotpt.common.task;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.service.JobPlanService;
import com.jsdc.iotpt.service.MissionItemRecordService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClassName: PlanTask
 * Description:
 * date: 2023/8/22 15:53
 *
 * @author bn
 */

public class PlanTask implements Task {

    private String planId;

    private String type;

    @Autowired
    private JobPlanService jobPlanService;
    @Autowired
    private MissionItemRecordService itemRecordService;


    public PlanTask(String type,String planId, JobPlanService jobPlanService, MissionItemRecordService itemRecordService) {
        this.type=type;
        this.planId = planId;
        this.jobPlanService = jobPlanService;
        this.itemRecordService = itemRecordService;
    }

    @Override
    public void execute() {
        JobPlan jobPlan=jobPlanService.getById(planId);
        Date currentTime=new Date();
        if (jobPlan.getExecuteType()==2){
            CronUtil.remove(type+planId);
        }else {
            // 重复执行时，当前时间超出执行期限，退出并删除；
            if (!(currentTime.after(jobPlan.getPlanStartTime())&&currentTime.before(jobPlan.getPlanEndTime()))){
                CronUtil.remove(type+planId);
                return;
            }
        }

        Mission mission=new Mission();
        mission.setNotes(jobPlan.getPlanName());
        mission.setTitle(jobPlan.getPlanName());
        mission.setSubstance(jobPlan.getPlanName());
        mission.setManageId(jobPlan.getManageId());
        mission.setSource(4);
        mission.setLevels(jobPlan.getPlanLevel());
        mission.setUserId(jobPlan.getCreateUser());
        mission.setStates(2);
        mission.setSourceId(jobPlan.getId());
        mission.setReportingTime(currentTime);
        mission.setIs_del(0);
        mission.setCreateUser(jobPlan.getCreateUser());
        // 计算一下执行期限
        if (jobPlan.getExecuteType()==1){
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(currentTime);
            if (jobPlan.getExecuteUint()==1){
                calendar.add(Calendar.HOUR_OF_DAY,jobPlan.getExecuteTimes());
            }else {
                calendar.add(Calendar.DAY_OF_MONTH,jobPlan.getExecuteTimes());
            }
            mission.setDeadlineTime(calendar.getTime());
        }
        itemRecordService.missionItemRecordSave(mission,jobPlan.getId());
    }

    public static void main(String[] args) throws ParseException {

        Date currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2024-02-28 23:59:59");
        Calendar calendar=Calendar.getInstance();

        calendar.setTime(currentTime);

        calendar.add(calendar.DAY_OF_MONTH,1);


        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));



    }

}
