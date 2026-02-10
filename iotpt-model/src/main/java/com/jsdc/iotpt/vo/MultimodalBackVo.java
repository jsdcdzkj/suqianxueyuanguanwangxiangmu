package com.jsdc.iotpt.vo;

import lombok.Data;

@Data
public class MultimodalBackVo {
    public String channelNo; // 设备编号
    public String deviceNo; // 通道编号
    public String imageUrl; // 告警图片Url
    public String time;     // 告警时间
    public String algId;    // 技能编号
    public String channelName;// 摄像机名称
    public String channelLocation;// 归属地 点位
}
