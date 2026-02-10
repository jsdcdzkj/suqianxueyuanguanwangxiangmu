package com.jsdc.iotpt.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.task.TaskManager;
import com.jsdc.iotpt.model.PatrolPlan;
import com.jsdc.iotpt.model.PatrolTimes;
import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.service.JobPlanService;
import com.jsdc.iotpt.service.PatrolPlanService;
import com.jsdc.iotpt.service.PatrolTimesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * ClassName: JobPlanConsumer
 * Description:
 * date: 2023/9/5 15:24
 * @author bn
 * todo 只能有一个服务 启动这个类 其他启动时需要 注释掉 @Component
 */
@Component
public class JobPlanConsumer implements InitializingBean, DisposableBean, Runnable {

    private static final Logger log = LoggerFactory.getLogger(JobPlanConsumer.class);
    @Value("${jobAddress}")
    private String ip;
    @Autowired
    private JobPlanService jobPlanService;
    @Autowired
    private PatrolPlanService patrolPlanService;
    @Autowired
    private PatrolTimesService timesService;
    @Autowired
    private TaskManager taskManager;


    @Override
    public void run() {
        //唤醒时间范围内需要执行的计划,已经执行过的单次计划因cron表达式缘故不会执行，所以不会重复生成任务
        List<JobPlan> jobPlans = jobPlanService.list(Wrappers.<JobPlan>lambdaQuery().
                //eq(JobPlan::getExecuteType, 1).
                eq(JobPlan::getIsDel, 0).
                eq(JobPlan::getPlanStatus, 0).
                apply("planStartTime <= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).
                apply("planEndTime >= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));


        jobPlans.forEach(x -> {
            taskManager.createTask(G.JOB_PLAN, String.valueOf(x.getId()), x.getCronExpression());
        });

        //唤醒时间范围内需要执行的计划,已经执行过的单次计划因cron表达式缘故不会执行，所以不会重复生成任务
        List<PatrolPlan> patrolPlans=patrolPlanService.list(Wrappers.<PatrolPlan>lambdaQuery().
//                        eq(PatrolPlan::getExecuteType, 1).
        eq(PatrolPlan::getIsDel, 0).
                        eq(PatrolPlan::getPlanStatus, 0).
                        and(x->x.
                                eq(PatrolPlan::getExecuteType, 0).
                                apply("cycleStartTime <= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).
                                apply("cycleEndTime >= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).
                                or().eq(PatrolPlan::getExecuteType, 1)
                        ));


        patrolPlans.forEach(x->{
            if (x.getExecuteType()==0){
                taskManager.createTask(G.PATROL_PLAN,String.valueOf(x.getId()),x.getCronExpression());
            }else {
                List<PatrolTimes> times=timesService.list(Wrappers.<PatrolTimes>lambdaQuery().
                        eq(PatrolTimes::getIsDel,0).
                        eq(PatrolTimes::getPlanId,x.getId()));

                for (PatrolTimes item:times) {
                    taskManager.createTask(G.PATROL_TIMES,String.valueOf(item.getId()),item.getCronExpression());
                }
            }
        });
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        if (Objects.equals(ip, hostAddress)){
            new Thread(this).start();
        }else {
            log.error(hostAddress);
            log.error("ip=====================本机器不支持启动Job，只一个可执行job");
        }
    }
}
