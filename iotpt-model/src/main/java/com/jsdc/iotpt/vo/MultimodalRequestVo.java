package com.jsdc.iotpt.vo;

import lombok.Data;

import java.util.List;

@Data
public class MultimodalRequestVo {
    public String url; // 接口预警接口
    public List<String> algId; // 技能编号
    public List<Channel> channels; // 摄像机 ，null为全部设备

    @Data
    public static class Channel{
        public String channelNo; // 通道编号
        public String deviceNo; // 设备编号
    }
}
