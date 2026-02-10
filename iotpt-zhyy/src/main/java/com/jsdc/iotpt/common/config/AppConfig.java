package com.jsdc.iotpt.common.config;


import com.jsdc.iotpt.service.AlarmRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 解决跨域问题
 */
@Configuration
public class AppConfig {


    @Autowired
    private AlarmRecordsService alarmRecordsService;


}


