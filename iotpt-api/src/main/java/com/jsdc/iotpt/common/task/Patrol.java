package com.jsdc.iotpt.common.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.service.PatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 巡更任务 更新状态
 */
@Component
public class Patrol {
    @Autowired
    private PatrolTaskService patrolTaskService;


    @Scheduled(fixedRate = 60000) // 每隔一分钟执行一次
    public void task1() {
        List<PatrolTask> list = patrolTaskService.list(Wrappers.<PatrolTask>lambdaQuery().eq(PatrolTask::getTaskStatus, "0"));
        String now = DateUtil.now();
        list.forEach(p -> {
            if (StringUtils.isNotEmpty(p.getCycleEndTime()) && now.equals(p.getCycleEndTime())) {
                //任务状态 0待处理 1已处理 2超时未处理 3缺卡
                if (null == p.getClockInCount()) {
                    p.setTaskStatus("2");
                    patrolTaskService.updateById(p);
                } else {
                    p.setTaskStatus("3");
                    patrolTaskService.updateById(p);
                }
            }
        });
    }
}
