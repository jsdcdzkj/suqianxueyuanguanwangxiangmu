package com.jsdc.iotpt.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author lb
 * @Date 2023/8/17 9:46
 * @Description 驾驶舱 实时报警信息 vo类
 **/
@Data
public class RealtimeWarningVo implements Serializable {
    private String imgUrl;
    private String name;
    private String time;
    private String location;
    private String warnType;
    private String type;//0:设备告警
}
