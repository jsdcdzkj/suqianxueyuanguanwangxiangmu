package com.jsdc.iotpt.vo;

import lombok.Data;

@Data
public class PatrolInfoVo {
    //巡更人员
    private String name ;

    //开始时间
    private String startTime ;

    //结束时间
    private String endTime ;

}
