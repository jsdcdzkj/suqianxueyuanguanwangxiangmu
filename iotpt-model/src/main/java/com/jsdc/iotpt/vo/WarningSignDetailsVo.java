package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.WarningSignDetails;
import lombok.Data;

import java.util.List;

@Data
public class WarningSignDetailsVo extends WarningSignDetails {
    private Integer pageNo = 1;
    private Integer pageSize = 10;


    // 告警等级名
    private String warnLevelName;

    // 区域名
    private String areaName;

    // 处理状态名称
    private String statusName;

    // 设备名称
    private String deviceName;

    // 开始时间
    private String startTime;

    // 结束时间
    private String endTime;

    private String name;
    private int type;
    private List<WarningSignDetails> domains;
    private List<WarningSignDetails> booleans;
    private List<Object> typeDes;
}
