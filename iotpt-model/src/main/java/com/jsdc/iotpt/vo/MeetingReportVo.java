package com.jsdc.iotpt.vo;

import lombok.Data;

@Data
public class MeetingReportVo {

    //时间类型 1 年度 2 月度 3 日
    private String timeType;

    private String startTime;
    private String endTime;

    //会议室类型 1:小会议室 2:大会议室 3:多功能会议室
    private Integer roomType;

    //组织单位id
    private Integer orgId;

    private String name;
    private Integer value;
    private Integer value1;
    private Integer value2;
    private Integer value3;
    private Integer count;
}
