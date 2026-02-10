package com.jsdc.iotpt.task;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 */
@Slf4j
@Component
public class LockTask {

    /**
     * 定时任务 查询地锁状态
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void execute() throws MqttException {

    }


}
