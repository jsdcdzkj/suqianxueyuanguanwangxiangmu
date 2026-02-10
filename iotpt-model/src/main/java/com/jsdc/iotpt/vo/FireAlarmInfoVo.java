package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.model.new_alarm.FireAlarmInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FireAlarmInfoVo extends FireAlarmInfo {
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private String startHanlder;//开始处理时间
    private String endHanlder;//结束处理时间
    private String startTime;//开始报警时间
    private String endTime;//结束报警事件
    private String warnSource;//报警来源
    private String area;//报警区域
    private String floor;//报警楼层
    private String ids;//id
    private AlarmRecords records;//报警信息



    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "报警区域")
    private String areaName;

    @ApiModelProperty(value = "报警内容")
    private String alarmContentStr;

    @ApiModelProperty(value = "报警时间")
    private String alarmTime;
}
