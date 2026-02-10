package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

@Data
public class AlarmStatisticsVo {
    private Integer id;

    private String name;
    private String value;
    /**
     * 1日
     * 2月
     * 3年
     * 4自定义
     */
    private String radio;// 日/月/年/自定义
    /**
     * 开始时间
     * 结束时间
     * 格式 yyyy-MM-dd HH:mm:ss
     */
    private String startTime;// 开始时间
    private String endTime;// 结束时间
    // 排名类型 1区域 2类型 3等级
    private String rankType;
    // 告警Code
    private String alarmCode;
    // 告警状态
    private List<String> handleStatus;
    private String alarmDate;
    private String alarmHour;

    //运维系统
    private String groupId;
    private String code;
    // 警种 1告警 2预警
    private String alarmType;
}
